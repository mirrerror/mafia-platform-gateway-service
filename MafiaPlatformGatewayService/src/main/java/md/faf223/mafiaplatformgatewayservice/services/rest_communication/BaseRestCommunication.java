package md.faf223.mafiaplatformgatewayservice.services.rest_communication;

import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.ServiceInstance;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceTimeoutException;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import md.faf223.mafiaplatformgatewayservice.services.ServiceCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

@Slf4j
public abstract class BaseRestCommunication {

    protected WebClient webClient;
    private final String serviceName;
    private DiscoveryServiceClient discoveryServiceClient;
    private final ServiceCircuitBreaker circuitBreaker;

    protected BaseRestCommunication(String serviceName, DiscoveryServiceClient discoveryServiceClient) {
        this.serviceName = serviceName;
        this.webClient = WebClient.builder().build();
        this.discoveryServiceClient = discoveryServiceClient;
        this.circuitBreaker = new ServiceCircuitBreaker(serviceName, discoveryServiceClient);
        log.info("{} communication initialized", serviceName);
    }
    
    protected ServiceInstance getServiceInstance() {
        try {
            ServiceInstance instance = discoveryServiceClient.getServiceInstance(serviceName);
            log.debug("Selected instance for {}: {}", serviceName, instance.getServiceUrl());
            return instance;
        } catch (Exception e) {
            log.error("Failed to get service URL for {}: {}", serviceName, e.getMessage());
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_DISCOVERY_FAILED\",\"message\":\"Could not discover %s\"}}", serviceName)
            );
        }
    }

    protected <T> T makeGetRequest(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making GET request to: {}", fullUrl);

        try {
            ApiResponse<T> response = webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
                circuitBreaker.recordSuccess();
                return response.getData();
            }
            circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("GET request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("GET request timeout to {}", fullUrl);
            circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("GET request error to {}: {}", fullUrl, e.getMessage(), e);
            circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePostRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making POST request to: {}", fullUrl);

        try {
            WebClient.RequestBodySpec requestSpec = webClient.post()
                    .uri(fullUrl);

            WebClient.ResponseSpec responseSpec;
            if (body != null) {
                responseSpec = requestSpec.bodyValue(body).retrieve();
            } else {
                responseSpec = requestSpec.retrieve();
            }

            ApiResponse<T> response = responseSpec
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
                circuitBreaker.recordSuccess();
                return response.getData();
            }
            circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("POST request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("POST request timeout to {}", fullUrl);
            circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("POST request error to {}: {}", fullUrl, e.getMessage(), e);
            circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePutRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making PUT request to: {}", fullUrl);

        try {
            WebClient.RequestBodySpec requestSpec = webClient.put()
                    .uri(fullUrl);

            WebClient.ResponseSpec responseSpec;
            if (body != null) {
                responseSpec = requestSpec.bodyValue(body).retrieve();
            } else {
                responseSpec = requestSpec.retrieve();
            }

            ApiResponse<T> response = responseSpec
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
               circuitBreaker.recordSuccess();
                return response.getData();
            }
           circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("PUT request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("PUT request timeout to {}", fullUrl);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("PUT request error to {}: {}", fullUrl, e.getMessage(), e);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T> T makeDeleteRequest(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making DELETE request to: {}", fullUrl);

        try {
            ApiResponse<T> response = webClient.delete()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
               circuitBreaker.recordSuccess();
                return response.getData();
            }
           circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("DELETE request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("DELETE request timeout to {}", fullUrl);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("DELETE request error to {}: {}", fullUrl, e.getMessage(), e);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    // ==================== OVERLOADED METHODS WITH HEADER SUPPORT ====================
    // These methods accept custom headers for authentication forwarding
    // while maintaining backward compatibility with existing services

    /**
     * Make a GET request with custom headers.
     * Used when the downstream service requires authentication headers from the gateway.
     *
     * @param uri The URI path (e.g., "/profile/123")
     * @param headers Custom headers to include (e.g., X-User-Id, X-Username)
     * @param typeRef The expected response type reference
     * @return The unwrapped data from ApiResponse
     */
    protected <T> T makeGetRequest(String uri, Map<String, String> headers, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making GET request to: {} with {} custom headers", fullUrl, headers != null ? headers.size() : 0);

        try {
            WebClient.RequestHeadersSpec<?> requestSpec = webClient.get().uri(fullUrl);

            // Add custom headers if provided
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestSpec.header(header.getKey(), header.getValue());
                    log.debug("Added header: {} = {}", header.getKey(), header.getValue());
                }
            }

            ApiResponse<T> response = requestSpec
                    .retrieve()
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
               circuitBreaker.recordSuccess();
                return response.getData();
            }
           circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("GET request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("GET request timeout to {}", fullUrl);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("GET request error to {}: {}", fullUrl, e.getMessage(), e);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    /**
     * Make a POST request with custom headers.
     * Used when the downstream service requires authentication headers from the gateway.
     *
     * @param uri The URI path (e.g., "/action")
     * @param body The request body
     * @param headers Custom headers to include (e.g., X-User-Id, X-Username)
     * @param typeRef The expected response type reference
     * @return The unwrapped data from ApiResponse
     */
    protected <T, R> T makePostRequest(String uri, R body, Map<String, String> headers, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making POST request to: {} with {} custom headers", fullUrl, headers != null ? headers.size() : 0);

        try {
            WebClient.RequestBodySpec requestSpec = webClient.post().uri(fullUrl);

            // Add custom headers if provided
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestSpec.header(header.getKey(), header.getValue());
                    log.debug("Added header: {} = {}", header.getKey(), header.getValue());
                }
            }

            WebClient.ResponseSpec responseSpec;
            if (body != null) {
                responseSpec = requestSpec.bodyValue(body).retrieve();
            } else {
                responseSpec = requestSpec.retrieve();
            }

            ApiResponse<T> response = responseSpec
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
               circuitBreaker.recordSuccess();
                return response.getData();
            }
           circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("POST request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("POST request timeout to {}", fullUrl);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("POST request error to {}: {}", fullUrl, e.getMessage(), e);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    /**
     * Make a PUT request with custom headers.
     * Used when the downstream service requires authentication headers from the gateway.
     *
     * @param uri The URI path (e.g., "/update/123")
     * @param body The request body
     * @param headers Custom headers to include (e.g., X-User-Id, X-Username)
     * @param typeRef The expected response type reference
     * @return The unwrapped data from ApiResponse
     */
    protected <T, R> T makePutRequest(String uri, R body, Map<String, String> headers, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making PUT request to: {} with {} custom headers", fullUrl, headers != null ? headers.size() : 0);

        try {
            WebClient.RequestBodySpec requestSpec = webClient.put().uri(fullUrl);

            // Add custom headers if provided
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestSpec.header(header.getKey(), header.getValue());
                    log.debug("Added header: {} = {}", header.getKey(), header.getValue());
                }
            }

            WebClient.ResponseSpec responseSpec;
            if (body != null) {
                responseSpec = requestSpec.bodyValue(body).retrieve();
            } else {
                responseSpec = requestSpec.retrieve();
            }

            ApiResponse<T> response = responseSpec
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
               circuitBreaker.recordSuccess();
                return response.getData();
            }
           circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("PUT request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("PUT request timeout to {}", fullUrl);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("PUT request error to {}: {}", fullUrl, e.getMessage(), e);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    /**
     * Make a DELETE request with custom headers.
     * Used when the downstream service requires authentication headers from the gateway.
     *
     * @param uri The URI path (e.g., "/delete/123")
     * @param headers Custom headers to include (e.g., X-User-Id, X-Username)
     * @param typeRef The expected response type reference
     * @return The unwrapped data from ApiResponse
     */
    protected <T> T makeDeleteRequest(String uri, Map<String, String> headers, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        circuitBreaker.checkState();
        ServiceInstance instance = getServiceInstance();
        String fullUrl = instance.getServiceUrl() + uri;
        log.info("Making DELETE request to: {} with {} custom headers", fullUrl, headers != null ? headers.size() : 0);

        try {
            WebClient.RequestHeadersSpec<?> requestSpec = webClient.delete().uri(fullUrl);

            // Add custom headers if provided
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestSpec.header(header.getKey(), header.getValue());
                    log.debug("Added header: {} = {}", header.getKey(), header.getValue());
                }
            }

            ApiResponse<T> response = requestSpec
                    .retrieve()
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
               circuitBreaker.recordSuccess();
                return response.getData();
            }
           circuitBreaker.recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("DELETE request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("DELETE request timeout to {}", fullUrl);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("DELETE request error to {}: {}", fullUrl, e.getMessage(), e);
           circuitBreaker.recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }
}
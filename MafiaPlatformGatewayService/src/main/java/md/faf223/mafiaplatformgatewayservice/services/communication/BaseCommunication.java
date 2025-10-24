package md.faf223.mafiaplatformgatewayservice.services.communication;

import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.ServiceInstance;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceTimeoutException;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Slf4j
public abstract class BaseCommunication {

    protected WebClient webClient;
    private final String serviceName;
    private DiscoveryServiceClient discoveryServiceClient;

    protected BaseCommunication(String serviceName) {
        this.serviceName = serviceName;
        this.webClient = WebClient.builder().build();
        log.info("{} communication initialized", serviceName);
    }

    protected BaseCommunication(String serviceName, DiscoveryServiceClient discoveryServiceClient) {
        this.serviceName = serviceName;
        this.webClient = WebClient.builder().build();
        this.discoveryServiceClient = discoveryServiceClient;
        log.info("{} communication initialized", serviceName);
    }

    protected String getServiceUrl() {
        try {
            ServiceInstance instance = discoveryServiceClient.getServiceInstance(serviceName);
            log.debug("Selected instance for {}: {}", serviceName, instance.getServiceUrl());
            return instance.getServiceUrl();
        } catch (Exception e) {
            log.error("Failed to get service URL for {}: {}", serviceName, e.getMessage());
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_DISCOVERY_FAILED\",\"message\":\"Could not discover %s\"}}", serviceName)
            );
        }
    }

    protected <T> T makeGetRequest(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        String baseUrl = getServiceUrl();
        String fullUrl = baseUrl + uri;
        log.info("Making GET request to: {}", fullUrl);

        try {
            ApiResponse<T> response = webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("GET request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("GET request timeout to {}", fullUrl);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            log.error("GET request error to {}: {}", fullUrl, e.getMessage(), e);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePostRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        String baseUrl = getServiceUrl();
        String fullUrl = baseUrl + uri;
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
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("POST request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("POST request timeout to {}", fullUrl);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            log.error("POST request error to {}: {}", fullUrl, e.getMessage(), e);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePutRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        String baseUrl = getServiceUrl();
        String fullUrl = baseUrl + uri;
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
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("PUT request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("PUT request timeout to {}", fullUrl);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            log.error("PUT request error to {}: {}", fullUrl, e.getMessage(), e);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T> T makeDeleteRequest(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        String baseUrl = getServiceUrl();
        String fullUrl = baseUrl + uri;
        log.info("Making DELETE request to: {}", fullUrl);

        try {
            ApiResponse<T> response = webClient.delete()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .timeout(Duration.ofMillis(5000))
                    .block();

            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("DELETE request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("DELETE request timeout to {}", fullUrl);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            log.error("DELETE request error to {}: {}", fullUrl, e.getMessage(), e);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }
}
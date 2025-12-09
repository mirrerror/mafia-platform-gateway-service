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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public abstract class BaseCommunication {

    protected WebClient webClient;
    private final String serviceName;
    private DiscoveryServiceClient discoveryServiceClient;

    private enum CircuitBreakerState {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private final Queue<Long> failureTimestamps = new ConcurrentLinkedQueue<>();
    private volatile long openUntilTimestamp = 0;

    private static final int FAILURE_THRESHOLD = 3;
    private static final long TIME_WINDOW_MS = 17500;
    private static final long OPEN_STATE_DURATION_MS = 60000;


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

    private synchronized void checkCircuitBreakerState() {
        if (state == CircuitBreakerState.OPEN) {
            long now = System.currentTimeMillis();
            if (now > openUntilTimestamp) {
                state = CircuitBreakerState.HALF_OPEN;
                log.warn("Circuit Breaker for {} is now HALF_OPEN. Allowing one probe request.", serviceName);
            } else {
                log.warn("Circuit Breaker for {} is OPEN. Request blocked.", serviceName);
                throw new MicroserviceException(503,
                        String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable (Circuit Breaker OPEN)\"}}", serviceName)
                );
            }
        }
    }

    private synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            log.info("Circuit Breaker for {} probe request SUCCEEDED. Closing the circuit.", serviceName);
            resetBreaker();
        }
        pruneOldFailures();
    }

    private synchronized void recordFailure(ServiceInstance instance) {
        long now = System.currentTimeMillis();
        if (state == CircuitBreakerState.HALF_OPEN) {
            log.warn("Circuit Breaker for {} probe request FAILED. Re-opening the circuit.", serviceName);
            tripBreaker(now, instance);
        } else {
            failureTimestamps.add(now);
            pruneOldFailures();

            if (failureTimestamps.size() >= FAILURE_THRESHOLD) {
                log.error("Circuit Breaker for {} FAILED {} times in window. Tripping circuit to OPEN.", serviceName, failureTimestamps.size());
                tripBreaker(now, instance);
            }
        }
    }

    private void pruneOldFailures() {
        long now = System.currentTimeMillis();
        long windowStart = now - TIME_WINDOW_MS;
        while (!failureTimestamps.isEmpty() && failureTimestamps.peek() < windowStart) {
            failureTimestamps.poll();
        }
    }

    private void tripBreaker(long now, ServiceInstance instanceToDeregister) {
        state = CircuitBreakerState.OPEN;
        openUntilTimestamp = now + OPEN_STATE_DURATION_MS;
        failureTimestamps.clear();

        if (instanceToDeregister != null) {
            log.error("Circuit Breaker for {} tripping. Deregistering instance: {}", serviceName, instanceToDeregister.getInstanceId());
            try {
                discoveryServiceClient.deregisterServiceInstance(instanceToDeregister.getInstanceId());
            } catch (Exception e) {
                log.error("Failed to deregister service instance {}: {}", instanceToDeregister.getInstanceId(), e.getMessage());
            }
        } else {
            log.error("Circuit Breaker for {} tripping, but no instance was provided to deregister.", serviceName);
        }
    }

    private void resetBreaker() {
        state = CircuitBreakerState.CLOSED;
        failureTimestamps.clear();
        openUntilTimestamp = 0;
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
        checkCircuitBreakerState();
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
                recordSuccess();
                return response.getData();
            }
            recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("GET request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("GET request timeout to {}", fullUrl);
            recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("GET request error to {}: {}", fullUrl, e.getMessage(), e);
            recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePostRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        checkCircuitBreakerState();
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
                recordSuccess();
                return response.getData();
            }
            recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("POST request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("POST request timeout to {}", fullUrl);
            recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("POST request error to {}: {}", fullUrl, e.getMessage(), e);
            recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T, R> T makePutRequest(String uri, R body, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        checkCircuitBreakerState();
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
                recordSuccess();
                return response.getData();
            }
            recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("PUT request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("PUT request timeout to {}", fullUrl);
            recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("PUT request error to {}: {}", fullUrl, e.getMessage(), e);
            recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }

    protected <T> T makeDeleteRequest(String uri, ParameterizedTypeReference<ApiResponse<T>> typeRef) {
        checkCircuitBreakerState();
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
                recordSuccess();
                return response.getData();
            }
            recordFailure(instance);
            throw new RuntimeException("Empty response from: " + fullUrl);
        } catch (WebClientResponseException e) {
            log.error("DELETE request failed to {}: Status={}, Body={}", fullUrl, e.getStatusCode(), e.getResponseBodyAsString());
            recordFailure(instance);
            throw new MicroserviceException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (TimeoutException e) {
            log.error("DELETE request timeout to {}", fullUrl);
            recordFailure(instance);
            throw new MicroserviceTimeoutException(503,
                    String.format("{\"error\":{\"code\":\"GATEWAY_TIMEOUT\",\"message\":\"Response from %s timed out\"}}", serviceName)
            );
        } catch (Exception e) {
            if (e instanceof MicroserviceException) throw (MicroserviceException) e;
            log.error("DELETE request error to {}: {}", fullUrl, e.getMessage(), e);
            recordFailure(instance);
            throw new MicroserviceException(503,
                    String.format("{\"error\":{\"code\":\"SERVICE_UNAVAILABLE\",\"message\":\"%s is currently unavailable\"}}", serviceName)
            );
        }
    }
}
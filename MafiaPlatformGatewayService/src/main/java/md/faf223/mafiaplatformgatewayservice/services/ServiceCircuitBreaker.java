package md.faf223.mafiaplatformgatewayservice.services;

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.ServiceInstance;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ServiceCircuitBreaker {

    private final String serviceName;
    private final DiscoveryServiceClient discoveryServiceClient;

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

    public ServiceCircuitBreaker(String serviceName, DiscoveryServiceClient discoveryServiceClient) {
        this.serviceName = serviceName;
        this.discoveryServiceClient = discoveryServiceClient;
        log.info("Circuit Breaker for {} initialized", serviceName);
    }

    public synchronized void checkState() {
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

    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            log.info("Circuit Breaker for {} probe request SUCCEEDED. Closing the circuit.", serviceName);
            resetBreaker();
        }
        pruneOldFailures();
    }

    public synchronized void recordFailure(ServiceInstance instance) {
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
}

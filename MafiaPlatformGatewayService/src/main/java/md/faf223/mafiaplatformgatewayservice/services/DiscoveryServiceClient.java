package md.faf223.mafiaplatformgatewayservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscoveryServiceClient {

    @Value("${DISCOVERY_SERVICE_URL}")
    private String discoveryServiceUrl;

    private final RestClient restClient;
    private final Random random = new Random();

    public ServiceInstance getServiceInstance(String serviceName) {
        try {
            String url = discoveryServiceUrl + "/api/discovery/services/" + serviceName;

            List<ServiceInstance> instances = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (instances == null || instances.isEmpty()) {
                throw new RuntimeException("No instances found for service: " + serviceName);
            }

            // Filter only UP instances
            List<ServiceInstance> upInstances = instances.stream()
                    .filter(instance -> "UP".equals(instance.getStatus()))
                    .filter(instance -> !instance.isCircuitBreakerOpen())
                    .toList();

            if (upInstances.isEmpty()) {
                throw new RuntimeException("No healthy instances found for service: " + serviceName);
            }

            // Random load balancing
            ServiceInstance selectedInstance = upInstances.get(random.nextInt(upInstances.size()));
            log.info("Selected instance for {}: {}", serviceName, selectedInstance.getServiceUrl());

            return selectedInstance;

        } catch (Exception e) {
            log.error("Error fetching service instance for {}: {}", serviceName, e.getMessage());
            throw new RuntimeException("Failed to discover service: " + serviceName, e);
        }
    }
}

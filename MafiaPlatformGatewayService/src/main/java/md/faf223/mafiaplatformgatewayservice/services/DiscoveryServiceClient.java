package md.faf223.mafiaplatformgatewayservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscoveryServiceClient {

    @Value("${DISCOVERY_SERVICE_URL}")
    private String discoveryServiceUrl;

    private final RestClient restClient;

    public ServiceInstance getServiceInstance(String serviceName) {
        try {
            String url = discoveryServiceUrl + "/api/discovery/services/" + serviceName + "/instance";

            ServiceInstance instance = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(ServiceInstance.class);

            if (instance == null) {
                throw new RuntimeException("No healthy instances found for service: " + serviceName);
            }

            log.info("Selected instance for {}: {}", serviceName, instance.getRPCUrl());
            return instance;
        } catch (Exception e) {
            log.error("Error fetching service instance for {}: {}", serviceName, e.getMessage());
            throw new RuntimeException("Failed to discover service: " + serviceName, e);
        }
    }

    public void deregisterServiceInstance(String instanceId) {
        try {
            String url = discoveryServiceUrl + "/api/discovery/deregister/" + instanceId;
            log.warn("Deregistering service instance: {}", instanceId);

            restClient.delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully deregistered service instance: {}", instanceId);
        } catch (Exception e) {
            log.error("Error deregistering service instance {}: {}", instanceId, e.getMessage());
            throw new RuntimeException("Failed to deregister service: " + instanceId, e);
        }
    }
}
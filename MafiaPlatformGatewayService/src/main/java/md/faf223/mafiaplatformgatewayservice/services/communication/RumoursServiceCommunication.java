package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice.PurchaseRumourDto;
import md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice.Rumour;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RumoursServiceCommunication extends BaseCommunication {

    public RumoursServiceCommunication(@Value("${RUMOURS_SERVICE_HOST}") String baseUrl,
                                       @Value("${RUMOURS_SERVICE_PORT}") String port,
                                       DiscoveryServiceClient discoveryServiceClient) {
        super("mafia-rumours-service", discoveryServiceClient);
    }

    public Rumour purchaseRumour(String lobbyId, PurchaseRumourDto purchaseRumourDto) {
        return makePostRequest(
                String.format("/api/rumours/%s/purchase", lobbyId),
                purchaseRumourDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public List<Rumour> getRumoursByOwner(String lobbyId, long ownerId) {
        return makeGetRequest(
                String.format("/api/rumours/%s/user/%d", lobbyId, ownerId),
                new ParameterizedTypeReference<>() {
                }
        );
    }

}
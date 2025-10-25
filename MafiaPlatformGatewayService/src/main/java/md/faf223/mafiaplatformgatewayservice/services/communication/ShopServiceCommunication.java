package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.shopservice.*;
import md.faf223.mafiaplatformgatewayservice.responses.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShopServiceCommunication extends BaseCommunication {

    public ShopServiceCommunication(@Value("${SHOP_SERVICE_HOST}") String baseUrl,
                                    @Value("${SHOP_SERVICE_PORT}") String port,
                                    DiscoveryServiceClient discoveryServiceClient) {
        super("shop-service", discoveryServiceClient);
    }


    public ApiResponse<Object> getItems(UUID gameId) {
        String uri = String.format("/items?gameId=%s", gameId);
        return makeGetRequest(
                uri,
                new ParameterizedTypeReference<>() {}
        );
    }

    public ApiResponse<Object> purchaseItem(PurchaseRequest request) {
        return makePostRequest(
                "/purchase",
                request,
                new ParameterizedTypeReference<>() {}
        );
    }

    public ApiResponse<Object> handlePhaseUpdate(PhaseUpdateRequest request) {
        return makePostRequest(
                "/phase-update",
                request,
                new ParameterizedTypeReference<>() {}
        );
    }

    public ApiResponse<Object> purchaseRumor(RumorPurchaseRequest request) {
        return makePostRequest(
                "/rumor-purchase",
                request,
                new ParameterizedTypeReference<>() {}
        );
    }
}
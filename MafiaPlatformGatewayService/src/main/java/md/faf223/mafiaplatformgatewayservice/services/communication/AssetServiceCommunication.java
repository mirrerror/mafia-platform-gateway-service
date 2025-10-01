package md.faf223.mafiaplatformgatewayservice.services.communication;

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.AssetDto;
import md.faf223.mafiaplatformgatewayservice.dtos.PlayerAssetsDto;
import md.faf223.mafiaplatformgatewayservice.responses.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class AssetServiceCommunication {

    private final WebClient webClient;

    public AssetServiceCommunication(@Value("${CHARACTER_SERVICE_HOST}") String baseUrl,
                                     @Value("${CHARACTER_SERVICE_PORT}") String port) {

        String fullBaseUrl = String.format("http://%s:%s", baseUrl, port);
        log.info("AssetServiceCommunication initialized with base URL: {}", fullBaseUrl);

        this.webClient = WebClient.builder()
                .baseUrl(fullBaseUrl)
                .build();

        log.info("WebClient created with base URL: {}", fullBaseUrl);
    }

    public List<String> getAllAssetSlots() {
        log.info("Calling getAllAssetSlots");

        try {
            String testResponse = webClient.get()
                    .uri("/assets/slots")  // Simple string
                    .retrieve()
                    .bodyToMono(String.class)  // Just get raw response
                    .block();

            log.info("Raw response: {}", testResponse);
        } catch (Exception e) {
            log.error("Failed to call service", e);
            throw e;
        }

        ParameterizedTypeReference<ApiResponse<AssetSlotsResponse>> typeRef =
                new ParameterizedTypeReference<>() {
                };

        log.info("Sending request to get all asset slots");
        ApiResponse<AssetSlotsResponse> apiResponse = webClient.get()
                .uri("/assets/slots")
                .retrieve()
                .bodyToMono(typeRef)
                .block();

        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData().getSlots();
        }

        throw new IllegalStateException("Failed to retrieve asset slots from inner service.");
    }


    public AssetResponse addAssetToPlayer(Long playerId, AssetDto request) {
        String url = "/" + playerId + "/assets";

        ParameterizedTypeReference<ApiResponse<AssetResponse>> typeRef =
                new ParameterizedTypeReference<>() {
                };

        ApiResponse<AssetResponse> apiResponse = webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(typeRef)
                .block();

        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData();
        }

        throw new IllegalStateException("Failed to add asset to player.");
    }

    public PlayerAssetsDto getPlayerAppearance(String playerId) {
        String url = "/" + playerId + "/appearance";

        ParameterizedTypeReference<ApiResponse<PlayerAssetsResponse>> typeRef =
                new ParameterizedTypeReference<>() {};

        ApiResponse<PlayerAssetsResponse> apiResponse = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(typeRef)
                .block();

        if (apiResponse != null && apiResponse.getData() != null && apiResponse.getData().getAssets() != null) {
            return apiResponse.getData().getAssets();
        }

        throw new IllegalStateException("Failed to get player appearance.");
    }

    public UpdateAssetResponse updatePlayerAsset(Long playerId, AssetDto request) {
        String url = "/" + playerId + "/assets";

        log.info("Updating asset for player: {}", playerId);
        log.info("Request body: slot={}, assetId={}", request.getSlot(), request.getAssetId());

        ParameterizedTypeReference<ApiResponse<UpdateAssetResponse>> typeRef =
                new ParameterizedTypeReference<>() {};

        try {
            ApiResponse<UpdateAssetResponse> apiResponse = webClient.put()
                    .uri(url)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("4xx error response body: {}", body);
                                    return new RuntimeException("Client error: " + body);
                                });
                    })
                    .bodyToMono(typeRef)
                    .block();

            if (apiResponse != null && apiResponse.getData() != null) {
                return apiResponse.getData();
            }
        } catch (Exception e) {
            log.error("Error updating asset", e);
            throw e;
        }

        throw new IllegalStateException("Failed to update player asset.");
    }
}

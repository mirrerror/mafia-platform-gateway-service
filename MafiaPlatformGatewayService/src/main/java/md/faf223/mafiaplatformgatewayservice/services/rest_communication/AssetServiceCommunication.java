package md.faf223.mafiaplatformgatewayservice.services.rest_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.AssetDto;
import md.faf223.mafiaplatformgatewayservice.dtos.PlayerAssetsDto;
import md.faf223.mafiaplatformgatewayservice.responses.AssetResponse;
import md.faf223.mafiaplatformgatewayservice.responses.AssetSlotsResponse;
import md.faf223.mafiaplatformgatewayservice.responses.PlayerAssetsResponse;
import md.faf223.mafiaplatformgatewayservice.responses.UpdateAssetResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceCommunication extends BaseRestCommunication {

    public AssetServiceCommunication(DiscoveryServiceClient discoveryServiceClient, DiscoveryServiceClient discoveryServiceClient1) {
        super("character-service", discoveryServiceClient);
    }

    public List<String> getAllAssetSlots() {
        AssetSlotsResponse response = makeGetRequest(
                "/assets/slots",
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getSlots();
    }

    public AssetResponse addAssetToPlayer(Long playerId, AssetDto request) {
        return makePostRequest(
                String.format("/%d/assets", playerId),
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public PlayerAssetsDto getPlayerAppearance(String playerId) {
        PlayerAssetsResponse response = makeGetRequest(
                String.format("/%s/appearance", playerId),
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getAssets();
    }

    public UpdateAssetResponse updatePlayerAsset(Long playerId, AssetDto request) {
        return makePutRequest(
                String.format("/%d/assets", playerId),
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
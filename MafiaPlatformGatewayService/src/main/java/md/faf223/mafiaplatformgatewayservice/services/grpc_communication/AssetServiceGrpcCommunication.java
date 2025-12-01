package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import com.dmitrii.characterservice.grpc.AssetServiceGrpc;
import com.dmitrii.characterservice.grpc.CharacterServiceProto.*;
import md.faf223.mafiaplatformgatewayservice.dtos.AssetDto;
import md.faf223.mafiaplatformgatewayservice.dtos.PlayerAssetsDto;
import md.faf223.mafiaplatformgatewayservice.responses.AssetResponse;
import md.faf223.mafiaplatformgatewayservice.responses.UpdateAssetResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetServiceGrpcCommunication extends BaseGrpcCommunication {

    public AssetServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("character-service", discoveryServiceClient);
    }

    public List<String> getAllAssetSlots() {
        GetAssetSlotsResponse grpcResponse = executeGrpcCall(channel -> {
            AssetServiceGrpc.AssetServiceBlockingStub stub = AssetServiceGrpc.newBlockingStub(channel);
            GetAssetSlotsRequest grpcRequest = GetAssetSlotsRequest.newBuilder().build();
            return stub.getAssetSlots(grpcRequest);
        });
        return new ArrayList<>(grpcResponse.getSlotsList());
    }

    public AssetResponse addAssetToPlayer(Long playerId, AssetDto request) {
        AddAssetResponse grpcResponse = executeGrpcCall(channel -> {
            AssetServiceGrpc.AssetServiceBlockingStub stub = AssetServiceGrpc.newBlockingStub(channel);
            AddAssetRequest grpcRequest = AddAssetRequest.newBuilder()
                    .setPlayerId(playerId)
                    .setAssetId(request.getAssetId())
                    .setSlot(request.getSlot())
                    .build();
            return stub.addAsset(grpcRequest);
        });

        return new AssetResponse(grpcResponse.getAssetId(), grpcResponse.getAdded());
    }

    public PlayerAssetsDto getPlayerAppearance(String playerId) {
        GetPlayerAppearanceResponse grpcResponse = executeGrpcCall(channel -> {
            AssetServiceGrpc.AssetServiceBlockingStub stub = AssetServiceGrpc.newBlockingStub(channel);
            GetPlayerAppearanceRequest grpcRequest = GetPlayerAppearanceRequest.newBuilder()
                    .setPlayerId(Long.parseLong(playerId))
                    .build();
            return stub.getPlayerAppearance(grpcRequest);
        });

        PlayerAssetsDto dto = new PlayerAssetsDto();
        if (grpcResponse.hasHair()) {
            dto.setHair(grpcResponse.getHair());
        }
        if (grpcResponse.hasShirt()) {
            dto.setShirt(grpcResponse.getShirt());
        }
        if (grpcResponse.hasPants()) {
            dto.setPants(grpcResponse.getPants());
        }
        dto.setAccessories(grpcResponse.getAccessoriesList());

        return dto;
    }

    public UpdateAssetResponse updatePlayerAsset(Long playerId, AssetDto request) {
        ChangeAssetResponse grpcResponse = executeGrpcCall(channel -> {
            AssetServiceGrpc.AssetServiceBlockingStub stub = AssetServiceGrpc.newBlockingStub(channel);
            ChangeAssetRequest grpcRequest = ChangeAssetRequest.newBuilder()
                    .setPlayerId(playerId)
                    .setAssetId(request.getAssetId())
                    .setSlot(request.getSlot())
                    .build();
            return stub.changeAsset(grpcRequest);
        });

        return new UpdateAssetResponse(grpcResponse.getSlot(), grpcResponse.getPreviousAssetId(), grpcResponse.getNewAssetId());
    }
}
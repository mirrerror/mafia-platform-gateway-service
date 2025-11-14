package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import com.dmitrii.characterservice.grpc.CharacterServiceProto;
import com.dmitrii.characterservice.grpc.ItemServiceGrpc;
import md.faf223.mafiaplatformgatewayservice.dtos.AddItemDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDataDto;
import md.faf223.mafiaplatformgatewayservice.dtos.ItemDto;
import md.faf223.mafiaplatformgatewayservice.responses.ItemRemovedResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ItemUsedResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceGrpcCommunication extends BaseGrpcCommunication {

    public ItemServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("character-service", discoveryServiceClient);
    }

    public List<ItemDto> getItemsForPlayer(Long playerId) {
        CharacterServiceProto.GetItemsResponse grpcResponse = executeGrpcCall(channel -> {
            ItemServiceGrpc.ItemServiceBlockingStub stub = ItemServiceGrpc.newBlockingStub(channel);
            CharacterServiceProto.GetItemsRequest grpcRequest = CharacterServiceProto.GetItemsRequest.newBuilder().setPlayerId(playerId).build();
            return stub.getItems(grpcRequest);
        });

        return grpcResponse.getItemsList().stream()
                .map(item -> new ItemDto(item.getId(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    public ItemDataDto addItemToPlayer(Long playerId, AddItemDto itemDto) {
        CharacterServiceProto.AddItemResponse grpcResponse = executeGrpcCall(channel -> {
            ItemServiceGrpc.ItemServiceBlockingStub stub = ItemServiceGrpc.newBlockingStub(channel);
            CharacterServiceProto.AddItemRequest grpcRequest = CharacterServiceProto.AddItemRequest.newBuilder()
                    .setPlayerId(playerId)
                    .setItemId(itemDto.getItemId())
                    .setQuantity(itemDto.getQuantity())
                    .build();
            return stub.addItem(grpcRequest);
        });

        return new ItemDataDto(grpcResponse.getItemId(), grpcResponse.getNewQuantity());
    }

    public ItemRemovedResponse removeItemFromPlayer(Long playerId, Long itemId) {
        CharacterServiceProto.RemoveItemResponse grpcResponse = executeGrpcCall(channel -> {
            ItemServiceGrpc.ItemServiceBlockingStub stub = ItemServiceGrpc.newBlockingStub(channel);
            CharacterServiceProto.RemoveItemRequest grpcRequest = CharacterServiceProto.RemoveItemRequest.newBuilder()
                    .setPlayerId(playerId)
                    .setItemId(itemId)
                    .build();
            return stub.removeItem(grpcRequest);
        });

        return new ItemRemovedResponse(grpcResponse.getItemId(), grpcResponse.getRemoved());
    }

    public ItemUsedResponse useItem(Long playerId, Long itemId) {
        CharacterServiceProto.UseItemResponse grpcResponse = executeGrpcCall(channel -> {
            ItemServiceGrpc.ItemServiceBlockingStub stub = ItemServiceGrpc.newBlockingStub(channel);
            CharacterServiceProto.UseItemRequest grpcRequest = CharacterServiceProto.UseItemRequest.newBuilder()
                    .setPlayerId(playerId)
                    .setItemId(itemId)
                    .build();
            return stub.useItem(grpcRequest);
        });

        return new ItemUsedResponse(grpcResponse.getItemId());
    }
}
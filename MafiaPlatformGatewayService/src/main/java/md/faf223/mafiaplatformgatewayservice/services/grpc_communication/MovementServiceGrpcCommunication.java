package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import com.dmitrii.townservice.grpc.MovementServiceGrpc;
import com.dmitrii.townservice.grpc.TownServiceProto;
import md.faf223.mafiaplatformgatewayservice.dtos.LocationVisitedDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MoveDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovePlayerDto;
import md.faf223.mafiaplatformgatewayservice.dtos.MovementDto;
import md.faf223.mafiaplatformgatewayservice.responses.PlayerMovementsResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovementServiceGrpcCommunication extends BaseGrpcCommunication {

    public MovementServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("town-service", discoveryServiceClient);
    }

    public MoveDto movePlayer(MovePlayerDto request) {
        TownServiceProto.MovePlayerResponse grpcResponse = executeGrpcCall(channel -> {
            // 1. Create stub
            MovementServiceGrpc.MovementServiceBlockingStub stub =
                    MovementServiceGrpc.newBlockingStub(channel);

            // 2. Convert REST DTO to gRPC Request
            TownServiceProto.MovePlayerRequest grpcRequest = TownServiceProto.MovePlayerRequest.newBuilder()
                    .setLobbyId(request.getLobbyId())
                    .setPlayerId(request.getPlayerId())
                    .setLocationId(request.getLocationId())
                    .build();

            // 3. Make the call
            return stub.movePlayer(grpcRequest);
        });

        // 4. Convert gRPC Response back to REST DTO
        return new MoveDto(
                grpcResponse.getPlayerId(),
                grpcResponse.getFromLocationId(),
                grpcResponse.getToLocationId(),
                grpcResponse.getTimestamp()
        );
    }

    public List<MovementDto> getAllMovements(String lobbyId) {
        TownServiceProto.GetAllMovementsResponse grpcResponse = executeGrpcCall(channel -> {
            MovementServiceGrpc.MovementServiceBlockingStub stub =
                    MovementServiceGrpc.newBlockingStub(channel);

            TownServiceProto.GetAllMovementsRequest grpcRequest = TownServiceProto.GetAllMovementsRequest.newBuilder()
                    .setLobbyId(Long.parseLong(lobbyId))
                    .build();

            return stub.getAllMovements(grpcRequest);
        });

        return grpcResponse.getMovementsList().stream()
                .map(MovementDto::new)
                .toList();
    }

    public PlayerMovementsResponse getMovementByPlayer(String lobbyId, String playerId) {
        TownServiceProto.GetMovementByPlayerResponse grpcResponse = executeGrpcCall(channel -> {
            MovementServiceGrpc.MovementServiceBlockingStub stub =
                    MovementServiceGrpc.newBlockingStub(channel);

            TownServiceProto.GetMovementByPlayerRequest grpcRequest = TownServiceProto.GetMovementByPlayerRequest.newBuilder()
                    .setLobbyId(Long.parseLong(lobbyId))
                    .setPlayerId(Long.parseLong(playerId))
                    .build();

            return stub.getMovementByPlayer(grpcRequest);
        });

        return new PlayerMovementsResponse(
                grpcResponse.getPlayerId(),
                grpcResponse.getLocationsVisitedList().stream()
                        .map(LocationVisitedDto::new)
                        .toList()
        );
    }

}

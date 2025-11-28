package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice.PurchaseRumourDto;
import md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice.Rumour;
import md.faf223.mafiaplatformgatewayservice.grpc.rumours.RumoursServiceGrpc;
import md.faf223.mafiaplatformgatewayservice.grpc.rumours.RumoursServiceProto;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RumoursServiceGrpcCommunication extends BaseGrpcCommunication {

    public RumoursServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("rumours-service", discoveryServiceClient);
    }

    public Rumour purchaseRumour(String lobbyId, PurchaseRumourDto dto) {
        RumoursServiceProto.RumourResponse response = executeGrpcCall(channel -> {
            var stub = RumoursServiceGrpc.newBlockingStub(channel);
            return stub.purchaseRumour(RumoursServiceProto.PurchaseRumourRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setGameId(dto.getGameId())
                    .setRumourType(dto.getRumourType())
                    .setSenderId(dto.getSenderId())
                    .setTargetId(dto.getTargetId())
                    .build());
        });
        return mapToRumour(response);
    }

    public List<Rumour> getRumoursByOwner(String lobbyId, long ownerId) {
        RumoursServiceProto.RumoursListResponse response = executeGrpcCall(channel -> {
            var stub = RumoursServiceGrpc.newBlockingStub(channel);
            return stub.getRumoursByOwner(RumoursServiceProto.GetRumoursRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setOwnerId(ownerId)
                    .build());
        });
        return response.getRumoursList().stream().map(this::mapToRumour).collect(Collectors.toList());
    }

    private Rumour mapToRumour(RumoursServiceProto.RumourResponse proto) {
        Rumour rumour = new Rumour();
        rumour.setId(proto.getId());
        rumour.setLobbyId(proto.getLobbyId());
        rumour.setType(proto.getType());
        rumour.setOwnerId(proto.getOwnerId());
        rumour.setTargetId(proto.getTargetId());
        rumour.setText(proto.getText());
        rumour.setCreatedAt(LocalDateTime.parse(proto.getCreatedAt()));
        return rumour;
    }

}
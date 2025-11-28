package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.*;
import md.faf223.mafiaplatformgatewayservice.grpc.communication.CommunicationServiceGrpc;
import md.faf223.mafiaplatformgatewayservice.grpc.communication.CommunicationServiceProto;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunicationServiceGrpcCommunication extends BaseGrpcCommunication {

    public CommunicationServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("mafia-communication-service", discoveryServiceClient);
    }

    public LobbyDto getLobby(String lobbyId) {
        CommunicationServiceProto.LobbyResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.getLobby(CommunicationServiceProto.LobbyRequest.newBuilder().setLobbyId(lobbyId).build());
        });
        return mapToLobbyDto(response);
    }

    public LobbyDto createLobby(LobbyCreationDto dto) {
        CommunicationServiceProto.LobbyResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);

            var channels = dto.getPrivateChannels().stream()
                    .map(pc -> CommunicationServiceProto.PrivateChannelInput.newBuilder()
                            .setChannelName(pc.getChannelName())
                            .addAllMemberIds(pc.getMemberIds())
                            .build())
                    .toList();

            return stub.createLobby(CommunicationServiceProto.CreateLobbyRequest.newBuilder()
                    .setLobbyId(dto.getLobbyId())
                    .addAllPrivateChannels(channels)
                    .build());
        });
        return mapToLobbyDto(response);
    }

    public DeleteLobbyResponseDto deleteLobby(String lobbyId) {
        CommunicationServiceProto.DeleteLobbyResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.deleteLobby(CommunicationServiceProto.LobbyRequest.newBuilder().setLobbyId(lobbyId).build());
        });
        DeleteLobbyResponseDto dto = new DeleteLobbyResponseDto();
        dto.setMessage(response.getMessage());
        return dto;
    }

    public ChatResponse sendGlobalMessage(String lobbyId, ChatMessage message) {
        CommunicationServiceProto.ChatResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.sendGlobalMessage(CommunicationServiceProto.GlobalMessageRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setSenderId(message.getSenderId())
                    .setSenderName(message.getSenderName())
                    .setContent(message.getContent())
                    .build());
        });
        return mapToChatResponse(response);
    }

    public List<ChatResponse> getGlobalChatHistory(String lobbyId) {
        CommunicationServiceProto.ChatHistoryResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.getGlobalChatHistory(CommunicationServiceProto.LobbyRequest.newBuilder().setLobbyId(lobbyId).build());
        });
        return response.getMessagesList().stream().map(this::mapToChatResponse).collect(Collectors.toList());
    }

    public GlobalChatStatusResponse toggleGlobalChat(String lobbyId) {
        CommunicationServiceProto.GlobalChatStatusResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.toggleGlobalChat(CommunicationServiceProto.LobbyRequest.newBuilder().setLobbyId(lobbyId).build());
        });
        return mapToStatusResponse(response);
    }

    public GlobalChatStatusResponse getGlobalChatStatus(String lobbyId) {
        CommunicationServiceProto.GlobalChatStatusResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.getGlobalChatStatus(CommunicationServiceProto.LobbyRequest.newBuilder().setLobbyId(lobbyId).build());
        });
        return mapToStatusResponse(response);
    }

    public PrivateChatResponse sendPrivateMessage(String lobbyId, String channelName, ChatMessage message) {
        CommunicationServiceProto.PrivateChatResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.sendPrivateMessage(CommunicationServiceProto.PrivateMessageRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setChannelName(channelName)
                    .setSenderId(message.getSenderId())
                    .setSenderName(message.getSenderName())
                    .setContent(message.getContent())
                    .build());
        });
        return mapToPrivateChatResponse(response);
    }

    public List<PrivateChatResponse> getPrivateChatHistory(String lobbyId, String channelName, long userId) {
        CommunicationServiceProto.PrivateChatHistoryResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.getPrivateChatHistory(CommunicationServiceProto.GetPrivateHistoryRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setChannelName(channelName)
                    .setUserId(userId)
                    .build());
        });
        return response.getMessagesList().stream().map(this::mapToPrivateChatResponse).collect(Collectors.toList());
    }

    public List<String> getPrivateChannels(String lobbyId) {
        CommunicationServiceProto.PrivateChannelsListResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.getPrivateChannels(CommunicationServiceProto.LobbyRequest.newBuilder().setLobbyId(lobbyId).build());
        });
        return response.getChannelsList();
    }

    public AnnouncementDto sendAnnouncement(String lobbyId, AnnouncementCreationDto dto) {
        CommunicationServiceProto.AnnouncementResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.sendAnnouncement(CommunicationServiceProto.AnnouncementRequest.newBuilder()
                    .setLobbyId(lobbyId)
                    .setContent(dto.getContent())
                    .build());
        });
        return mapToAnnouncementDto(response);
    }

    public List<AnnouncementDto> getAnnouncementHistory(String lobbyId) {
        CommunicationServiceProto.AnnouncementHistoryResponse response = executeGrpcCall(channel -> {
            var stub = CommunicationServiceGrpc.newBlockingStub(channel);
            return stub.getAnnouncementHistory(CommunicationServiceProto.LobbyRequest.newBuilder().setLobbyId(lobbyId).build());
        });
        return response.getAnnouncementsList().stream().map(this::mapToAnnouncementDto).collect(Collectors.toList());
    }

    private LobbyDto mapToLobbyDto(CommunicationServiceProto.LobbyResponse proto) {
        LobbyDto dto = new LobbyDto();
        dto.setId(proto.getId());

        Map<String, PrivateChannelResponseDto> channels = proto.getPrivateChannelsMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            PrivateChannelResponseDto p = new PrivateChannelResponseDto();
                            p.setName(e.getValue().getName());
                            p.setMembers(e.getValue().getMembersMap());
                            return p;
                        }
                ));
        dto.setPrivateChannels(channels);
        return dto;
    }

    private ChatResponse mapToChatResponse(CommunicationServiceProto.ChatResponse proto) {
        ChatResponse response = new ChatResponse();
        response.setLobbyId(proto.getLobbyId());
        response.setSenderId(proto.getSenderId());
        response.setSenderName(proto.getSenderName());
        response.setContent(proto.getContent());
        response.setTimestamp(LocalDateTime.parse(proto.getTimestamp()));
        return response;
    }

    private PrivateChatResponse mapToPrivateChatResponse(CommunicationServiceProto.PrivateChatResponse proto) {
        PrivateChatResponse response = new PrivateChatResponse();
        response.setLobbyId(proto.getLobbyId());
        response.setChannelName(proto.getChannelName());
        response.setSenderId(proto.getSenderId());
        response.setSenderName(proto.getSenderName());
        response.setContent(proto.getContent());
        response.setTimestamp(LocalDateTime.parse(proto.getTimestamp()));
        return response;
    }

    private GlobalChatStatusResponse mapToStatusResponse(CommunicationServiceProto.GlobalChatStatusResponse proto) {
        GlobalChatStatusResponse response = new GlobalChatStatusResponse();
        response.setLobbyId(proto.getLobbyId());
        response.setGlobalChatEnabled(proto.getIsGlobalChatEnabled());
        return response;
    }

    private AnnouncementDto mapToAnnouncementDto(CommunicationServiceProto.AnnouncementResponse proto) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(proto.getId());
        dto.setLobbyId(proto.getLobbyId());
        dto.setContent(proto.getContent());
        dto.setTimestamp(LocalDateTime.parse(proto.getTimestamp()));
        return dto;
    }

}
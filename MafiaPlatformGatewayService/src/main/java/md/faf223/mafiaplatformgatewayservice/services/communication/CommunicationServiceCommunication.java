package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.AnnouncementCreationDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.AnnouncementDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatMessage;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatResponse;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.DeleteLobbyResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.GlobalChatStatusResponse;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.LobbyCreationDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.LobbyDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.PrivateChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunicationServiceCommunication extends BaseCommunication {

    public CommunicationServiceCommunication(@Value("${COMMUNICATION_SERVICE_HOST}") String baseUrl,
                                             @Value("${COMMUNICATION_SERVICE_PORT}") String port) {
        super(baseUrl, port, "CommunicationService");
    }

    public LobbyDto getLobby(String lobbyId) {
        String uri = "/api/chat/lobby/" + lobbyId;
        return makeGetRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

    public LobbyDto createLobby(LobbyCreationDto lobbyCreationDto) {
        String uri = "/api/chat/lobby/create";
        return makePostRequest(uri, lobbyCreationDto, new ParameterizedTypeReference<>() {
        });
    }

    public DeleteLobbyResponseDto deleteLobby(String lobbyId) {
        String uri = "/api/chat/lobby/" + lobbyId;
        return makeDeleteRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

    public ChatResponse sendGlobalMessage(String lobbyId, ChatMessage message) {
        String uri = "/api/chat/global/" + lobbyId + "/send-message";
        return makePostRequest(uri, message, new ParameterizedTypeReference<>() {
        });
    }

    public List<ChatResponse> getGlobalChatHistory(String lobbyId) {
        String uri = "/api/chat/global/" + lobbyId + "/history";
        return makeGetRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

    public GlobalChatStatusResponse toggleGlobalChat(String lobbyId) {
        String uri = "/api/chat/global/" + lobbyId + "/toggle";
        return makePostRequest(uri, null, new ParameterizedTypeReference<>() {
        });
    }

    public GlobalChatStatusResponse getGlobalChatStatus(String lobbyId) {
        String uri = "/api/chat/global/" + lobbyId + "/status";
        return makeGetRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

    public PrivateChatResponse sendPrivateMessage(String lobbyId, String channelName, ChatMessage message) {
        String uri = String.format("/api/chat/private/%s/%s/send-message", lobbyId, channelName);
        return makePostRequest(uri, message, new ParameterizedTypeReference<>() {
        });
    }

    public List<PrivateChatResponse> getPrivateChatHistory(String lobbyId, String channelName, long userId) {
        String uri = String.format("/api/chat/private/%s/%s/history?userId=%d", lobbyId, channelName, userId);
        return makeGetRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

    public List<String> getPrivateChannels(String lobbyId) {
        String uri = "/api/chat/private/" + lobbyId + "/channels";
        return makeGetRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

    public AnnouncementDto sendAnnouncement(String lobbyId, AnnouncementCreationDto announcementCreationDto) {
        return makePostRequest(
                String.format("/api/chat/announcement/%s", lobbyId),
                announcementCreationDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public List<AnnouncementDto> getAnnouncementHistory(String lobbyId) {
        return makeGetRequest(
                String.format("/api/chat/announcement/%s/history", lobbyId),
                new ParameterizedTypeReference<>() {
                }
        );
    }

}
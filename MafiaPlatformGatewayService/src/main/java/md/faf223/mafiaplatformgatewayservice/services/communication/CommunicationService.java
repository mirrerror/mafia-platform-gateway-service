package md.faf223.mafiaplatformgatewayservice.services.communication;

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommunicationService extends BaseCommunication {

    public CommunicationService(@Value("${COMMUNICATION_SERVICE_HOST}") String baseUrl,
                                @Value("${COMMUNICATION_SERVICE_PORT}") String port) {
        super(baseUrl, port, "CommunicationService");
    }

    public Object getLobby(String lobbyId) {
        String uri = "/api/chat/lobby/" + lobbyId;
        return makeGetRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

    public Object createLobby(LobbyCreationDto lobbyCreationDto) {
        String uri = "/api/chat/lobby/create";
        return makePostRequest(uri, lobbyCreationDto, new ParameterizedTypeReference<>() {
        });
    }

    public void deleteLobby(String lobbyId) {
        String uri = "/api/chat/lobby/" + lobbyId;
        makeDeleteRequest(uri, new ParameterizedTypeReference<>() {
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

    public List<Object> getPrivateChannels(String lobbyId) {
        String uri = "/api/chat/private/" + lobbyId + "/channels";
        return makeGetRequest(uri, new ParameterizedTypeReference<>() {
        });
    }

}
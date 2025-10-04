package md.faf223.mafiaplatformgatewayservice.services.communication;

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class CommunicationService {

    private final WebClient webClient;

    public CommunicationService(@Value("${COMMUNICATION_SERVICE_HOST}") String baseUrl,
                                @Value("${COMMUNICATION_SERVICE_PORT}") String port) {

        String fullBaseUrl = String.format("http://%s:%s/api/chat", baseUrl, port);
        log.info("CommunicationService initialized with base URL: {}", fullBaseUrl);

        this.webClient = WebClient.builder()
                .baseUrl(fullBaseUrl)
                .build();

        log.info("WebClient created with base URL: {}", fullBaseUrl);
    }

    public Object getLobby(String lobbyId) {
        String uri = "/lobby/" + lobbyId;
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object createLobby(LobbyCreationDto lobbyCreationDto) {
        String uri = "/lobby/create";
        return webClient.post()
                .uri(uri)
                .bodyValue(lobbyCreationDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public void deleteLobby(String lobbyId) {
        String uri = "/lobby/" + lobbyId;
        webClient.delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ChatResponse sendGlobalMessage(String lobbyId, ChatMessage message) {
        String uri = "/global/" + lobbyId + "/send-message";
        return webClient.post()
                .uri(uri)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();
    }

    public List<ChatResponse> getGlobalChatHistory(String lobbyId) {
        String uri = "/global/" + lobbyId + "/history";
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(ChatResponse.class)
                .collectList()
                .block();
    }

    public GlobalChatStatusResponse toggleGlobalChat(String lobbyId) {
        String uri = "/global/" + lobbyId + "/toggle";
        return webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(GlobalChatStatusResponse.class)
                .block();
    }

    public GlobalChatStatusResponse getGlobalChatStatus(String lobbyId) {
        String uri = "/global/" + lobbyId + "/status";
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(GlobalChatStatusResponse.class)
                .block();
    }

    public PrivateChatResponse sendPrivateMessage(String lobbyId, String channelName, ChatMessage message) {
        String uri = String.format("/private/%s/%s/send-message", lobbyId, channelName);
        return webClient.post()
                .uri(uri)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(PrivateChatResponse.class)
                .block();
    }

    public List<PrivateChatResponse> getPrivateChatHistory(String lobbyId, String channelName, long userId) {
        String uri = String.format("/private/%s/%s/history?userId=%d", lobbyId, channelName, userId);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(PrivateChatResponse.class)
                .collectList()
                .block();
    }

    public List<Object> getPrivateChannels(String lobbyId) {
        String uri = "/private/" + lobbyId + "/channels";
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .block();
    }

}
package md.faf223.mafiaplatformgatewayservice.services.rest_communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.microsoft.signalr.GsonHubProtocol;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.WebSocketSenderService;
import md.faf223.mafiaplatformgatewayservice.utils.LocalDateTimeAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignalRBridgeService {

    @Value("${COMMUNICATION_SERVICE_HOST}")
    private String communicationServiceHost;

    @Value("${COMMUNICATION_SERVICE_PORT}")
    private String communicationServicePort;

    private final WebSocketSenderService webSocketSenderService;
    private final CacheManager cacheManager;
    private HubConnection hubConnection;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @PostConstruct
    public void init() {
        String hubUrl = String.format("http://%s:%s/chathub", communicationServiceHost, communicationServicePort);
        log.info("Initializing SignalR Hub connection to: {}", hubUrl);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        hubConnection = HubConnectionBuilder.create(hubUrl)
                .withHubProtocol(new GsonHubProtocol(gson))
                .build();

        hubConnection.on("ReceiveGlobalMessage", (ApiResponse<ChatResponse> response) -> {
            log.info("Received global message from SignalR, forwarding to STOMP topic and evicting cache");
            String lobbyId = response.getData().getLobbyId();
            String destination = "/api/topic/chat/global/" + lobbyId;

            evictCache("globalChatHistory", lobbyId);

            webSocketSenderService.sendMessageToClients(destination, response);
        }, new ParameterizedTypeReference<ApiResponse<ChatResponse>>() {}.getType());

        hubConnection.on("ReceivePrivateMessage", (ApiResponse<PrivateChatResponse> response) -> {
            log.info("Received private message from SignalR, forwarding to STOMP topic and evicting cache");
            String lobbyId = response.getData().getLobbyId();
            String channelName = response.getData().getChannelName();
            String destination = String.format("/api/topic/chat/private/%s/%s", lobbyId, channelName);

            String cacheKey = lobbyId + "_" + channelName;
            evictCache("privateChatHistory", cacheKey);

            webSocketSenderService.sendMessageToClients(destination, response);
        }, new ParameterizedTypeReference<ApiResponse<PrivateChatResponse>>() {}.getType());

        hubConnection.on("ReceiveAnnouncement", (ApiResponse<AnnouncementDto> response) -> {
            log.info("Received announcement from SignalR, forwarding to STOMP topic and evicting cache");
            String lobbyId = response.getData().getLobbyId();
            String destination = "/api/topic/chat/announcement/" + lobbyId;

            evictCache("announcements", lobbyId);

            webSocketSenderService.sendMessageToClients(destination, response);
        }, new ParameterizedTypeReference<ApiResponse<AnnouncementDto>>() {}.getType());

        hubConnection.on("GlobalChatStatusChanged", (ApiResponse<GlobalChatStatusResponse> response) -> {
            String lobbyId = response.getData().getLobbyId();
            log.info("Received 'GlobalChatStatusChanged' for lobby '{}', forwarding to STOMP topic.", lobbyId);

            evictCache("chatStatus", lobbyId);

            String destination = "/api/topic/chat/status/" + lobbyId;
            webSocketSenderService.sendMessageToClients(destination, response);
        }, new ParameterizedTypeReference<ApiResponse<GlobalChatStatusResponse>>() {}.getType());

        try {
            hubConnection.start().blockingAwait();
            log.info("SignalR Hub connection established.");
        } catch (Exception e) {
            log.error("Failed to connect to SignalR Hub: {}", e.getMessage());
        }
    }

    private void evictCache(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("Cache evicted for '{}' with key '{}'", cacheName, key);
        } else {
            log.warn("Cache '{}' not found. Could not evict key '{}'", cacheName, key);
        }
    }

    public void sendGlobalMessage(String lobbyId, ChatMessage message) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            disposables.add(
                    hubConnection.invoke("SendGlobalMessage", lobbyId, message)
                            .subscribe(() -> {},
                                    error -> log.error("Error sending global message via SignalR: {}", error.getMessage()))
            );
        } else {
            log.error("Cannot send global message. SignalR connection is not active.");
        }
    }

    public void sendPrivateMessage(String lobbyId, String channelName, ChatMessage message) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            disposables.add(
                    hubConnection.invoke("SendPrivateMessage", channelName, lobbyId, message)
                            .subscribe(() -> {},
                                    error -> log.error("Error sending private message via SignalR: {}", error.getMessage()))
            );
        } else {
            log.error("Cannot send private message. SignalR connection is not active.");
        }
    }

    public void joinGlobalChat(String lobbyId, long userId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            disposables.add(
                    hubConnection.invoke("JoinGlobalChat", lobbyId, userId)
                            .subscribe(() -> {},
                                    error -> log.error("Error joining global chat via SignalR: {}", error.getMessage()))
            );
        } else {
            log.error("Cannot join global chat. SignalR connection is not active.");
        }
    }

    public void leaveGlobalChat(String lobbyId, long userId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            disposables.add(
                    hubConnection.invoke("LeaveGlobalChat", lobbyId, userId)
                            .subscribe(() -> {},
                                    error -> log.error("Error leaving global chat via SignalR: {}", error.getMessage()))
            );
        } else {
            log.error("Cannot leave global chat. SignalR connection is not active.");
        }
    }

    public void joinPrivateChannel(String lobbyId, String channelName, long userId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            disposables.add(
                    hubConnection.invoke("JoinPrivateChannel", lobbyId, channelName, userId)
                            .subscribe(() -> {},
                                    error -> log.error("Error joining private channel via SignalR: {}", error.getMessage()))
            );
        } else {
            log.error("Cannot join private channel. SignalR connection is not active.");
        }
    }

    public void leavePrivateChannel(String lobbyId, String channelName, long userId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            disposables.add(
                    hubConnection.invoke("LeavePrivateChannel", lobbyId, channelName, userId)
                            .subscribe(() -> {},
                                    error -> log.error("Error leaving private channel via SignalR: {}", error.getMessage()))
            );
        } else {
            log.error("Cannot leave private channel. SignalR connection is not active.");
        }
    }

    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up SignalR resources.");
        disposables.dispose();
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.close();
        }
    }

}
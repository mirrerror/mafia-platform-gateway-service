package md.faf223.mafiaplatformgatewayservice.services.communication;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatMessage;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatResponse;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.PrivateChatResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.WebSocketSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignalRBridgeService {

    @Value("${COMMUNICATION_SERVICE_HOST}")
    private String communicationServiceHost;

    @Value("${COMMUNICATION_SERVICE_PORT}")
    private String communicationServicePort;

    private final WebSocketSenderService webSocketSenderService;
    private HubConnection hubConnection;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @PostConstruct
    public void init() {
        String hubUrl = String.format("http://%s:%s/chathub", communicationServiceHost, communicationServicePort);
        log.info("Initializing SignalR Hub connection to: {}", hubUrl);

        hubConnection = HubConnectionBuilder.create(hubUrl)
                .build();

        hubConnection.on("ReceiveGlobalMessage", (ApiResponse<ChatResponse> response) -> {
            log.info("Received global message from SignalR, forwarding to STOMP topic");
            String destination = "/api/topic/chat/global/" + response.getData().getLobbyId();
            webSocketSenderService.sendMessageToClients(destination, response);
        }, new ParameterizedTypeReference<ApiResponse<ChatResponse>>() {}.getType());

        hubConnection.on("ReceivePrivateMessage", (ApiResponse<PrivateChatResponse> response) -> {
            log.info("Received private message from SignalR, forwarding to STOMP topic");
            String destination = String.format("/api/topic/chat/private/%s/%s", response.getData().getLobbyId(), response.getData().getChannelName());
            webSocketSenderService.sendMessageToClients(destination, response);
        }, new ParameterizedTypeReference<ApiResponse<PrivateChatResponse>>() {}.getType());


        try {
            hubConnection.start().blockingAwait();
            log.info("SignalR Hub connection established.");
        } catch (Exception e) {
            log.error("Failed to connect to SignalR Hub: {}", e.getMessage());
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
                    hubConnection.invoke("SendPrivateMessage", lobbyId, channelName, message)
                            .subscribe(() -> {},
                                    error -> log.error("Error sending private message via SignalR: {}", error.getMessage()))
            );
        } else {
            log.error("Cannot send private message. SignalR connection is not active.");
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
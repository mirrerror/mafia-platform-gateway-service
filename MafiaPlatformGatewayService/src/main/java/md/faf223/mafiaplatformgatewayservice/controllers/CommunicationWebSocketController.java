package md.faf223.mafiaplatformgatewayservice.controllers;

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatMessage;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatResponse;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.PrivateChatResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.WebSocketSenderService;
import md.faf223.mafiaplatformgatewayservice.services.rest_communication.SignalRBridgeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class CommunicationWebSocketController {

    private final SignalRBridgeService signalRBridgeService;
    private final WebSocketSenderService webSocketSenderService;

    @MessageMapping("/chat/global/{lobbyId}/send-message")
    @CacheEvict(value = "globalChatHistory", key = "#lobbyId")
    public void handleGlobalMessage(@DestinationVariable String lobbyId, @Payload ChatMessage message) {
        signalRBridgeService.sendGlobalMessage(lobbyId, message);

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setLobbyId(lobbyId);
        chatResponse.setSenderId(message.getSenderId());
        chatResponse.setSenderName(message.getSenderName());
        chatResponse.setContent(message.getContent());
        chatResponse.setTimestamp(LocalDateTime.now());

        ApiResponse<ChatResponse> apiResponse = new ApiResponse<>(chatResponse);

        String topic = String.format("/api/topic/chat/global/%s", lobbyId);
        webSocketSenderService.sendMessageToClients(topic, apiResponse);
    }

    @MessageMapping("/chat/private/{lobbyId}/{channelName}/send-message")
    @CacheEvict(value = "privateChatHistory", key = "#lobbyId + '_' + #channelName")
    public void handlePrivateMessage(
            @DestinationVariable String lobbyId,
            @DestinationVariable String channelName,
            @Payload ChatMessage message) {
        signalRBridgeService.sendPrivateMessage(lobbyId, channelName, message);

        PrivateChatResponse chatResponse = new PrivateChatResponse();
        chatResponse.setLobbyId(lobbyId);
        chatResponse.setChannelName(channelName);
        chatResponse.setSenderId(message.getSenderId());
        chatResponse.setSenderName(message.getSenderName());
        chatResponse.setContent(message.getContent());
        chatResponse.setTimestamp(LocalDateTime.now());

        ApiResponse<PrivateChatResponse> apiResponse = new ApiResponse<>(chatResponse);

        String topic = String.format("/api/topic/chat/private/%s/%s", lobbyId, channelName);
        webSocketSenderService.sendMessageToClients(topic, apiResponse);
    }

    @MessageMapping("/chat/global/{lobbyId}/join")
    public void handleJoinGlobalChat(@DestinationVariable String lobbyId, @Payload long userId) {
        signalRBridgeService.joinGlobalChat(lobbyId, userId);
    }

    @MessageMapping("/chat/global/{lobbyId}/leave")
    public void handleLeaveGlobalChat(@DestinationVariable String lobbyId, @Payload long userId) {
        signalRBridgeService.leaveGlobalChat(lobbyId, userId);
    }

    @MessageMapping("/chat/private/{lobbyId}/{channelName}/join")
    public void handleJoinPrivateChannel(@DestinationVariable String lobbyId, @DestinationVariable String channelName, @Payload long userId) {
        signalRBridgeService.joinPrivateChannel(lobbyId, channelName, userId);
    }

    @MessageMapping("/chat/private/{lobbyId}/{channelName}/leave")
    public void handleLeavePrivateChannel(@DestinationVariable String lobbyId, @DestinationVariable String channelName, @Payload long userId) {
        signalRBridgeService.leavePrivateChannel(lobbyId, channelName, userId);
    }

}
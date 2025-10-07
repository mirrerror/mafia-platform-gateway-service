package md.faf223.mafiaplatformgatewayservice.controllers;

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatMessage;
import md.faf223.mafiaplatformgatewayservice.services.communication.SignalRBridgeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CommunicationWebSocketController {

    private final SignalRBridgeService signalRBridgeService;

    @MessageMapping("/chat/global/{lobbyId}/send-message")
    @CacheEvict(value = "globalChatHistory", key = "#lobbyId")
    public void handleGlobalMessage(@DestinationVariable String lobbyId, ChatMessage message) {
        signalRBridgeService.sendGlobalMessage(lobbyId, message);
    }

    @MessageMapping("/chat/private/{lobbyId}/{channelName}/send-message")
    @CacheEvict(value = "privateChatHistory", key = "#lobbyId + '_' + #channelName")
    public void handlePrivateMessage(@DestinationVariable String lobbyId, @DestinationVariable String channelName, ChatMessage message) {
        signalRBridgeService.sendPrivateMessage(lobbyId, channelName, message);
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
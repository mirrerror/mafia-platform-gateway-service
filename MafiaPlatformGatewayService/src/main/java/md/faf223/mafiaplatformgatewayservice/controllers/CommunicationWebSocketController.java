package md.faf223.mafiaplatformgatewayservice.controllers;

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatMessage;
import md.faf223.mafiaplatformgatewayservice.services.communication.SignalRBridgeService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CommunicationWebSocketController {

    private final SignalRBridgeService signalRBridgeService;

    @MessageMapping("/chat/global/{lobbyId}/send-message")
    public void handleGlobalMessage(@DestinationVariable String lobbyId, ChatMessage message) {
        signalRBridgeService.sendGlobalMessage(lobbyId, message);
    }

    @MessageMapping("/chat/private/{lobbyId}/{channelName}/send-message")
    public void handlePrivateMessage(@DestinationVariable String lobbyId, @DestinationVariable String channelName, ChatMessage message) {
        signalRBridgeService.sendPrivateMessage(lobbyId, channelName, message);
    }

}
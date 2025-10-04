package md.faf223.mafiaplatformgatewayservice.controllers;

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatMessage;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.LobbyCreationDto;
import md.faf223.mafiaplatformgatewayservice.services.communication.CommunicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;

    @GetMapping("lobby/{lobbyId}")
    public ResponseEntity<Object> getLobby(@PathVariable String lobbyId) {
        return ResponseEntity.ok(communicationService.getLobby(lobbyId));
    }

    @PostMapping("lobby/create")
    public ResponseEntity<Object> createLobby(@RequestBody LobbyCreationDto lobbyCreationDto) {
        return ResponseEntity.ok(communicationService.createLobby(lobbyCreationDto));
    }

    @DeleteMapping("lobby/{lobbyId}")
    public ResponseEntity<Void> deleteLobby(@PathVariable String lobbyId) {
        communicationService.deleteLobby(lobbyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("global/{lobbyId}/send-message")
    public ResponseEntity<Object> sendGlobalMessage(@PathVariable String lobbyId, @RequestBody ChatMessage message) {
        return ResponseEntity.ok(communicationService.sendGlobalMessage(lobbyId, message));
    }

    @GetMapping("global/{lobbyId}/history")
    public ResponseEntity<Object> getGlobalChatHistory(@PathVariable String lobbyId) {
        return ResponseEntity.ok(communicationService.getGlobalChatHistory(lobbyId));
    }

    @PostMapping("global/{lobbyId}/toggle")
    public ResponseEntity<Object> toggleGlobalChat(@PathVariable String lobbyId) {
        return ResponseEntity.ok(communicationService.toggleGlobalChat(lobbyId));
    }

    @GetMapping("global/{lobbyId}/status")
    public ResponseEntity<Object> getGlobalChatStatus(@PathVariable String lobbyId) {
        return ResponseEntity.ok(communicationService.getGlobalChatStatus(lobbyId));
    }

    @PostMapping("private/{lobbyId}/{channelName}/send-message")
    public ResponseEntity<Object> sendPrivateMessage(@PathVariable String lobbyId, @PathVariable String channelName, @RequestBody ChatMessage message) {
        return ResponseEntity.ok(communicationService.sendPrivateMessage(lobbyId, channelName, message));
    }

    @GetMapping("private/{lobbyId}/{channelName}/history")
    public ResponseEntity<Object> getPrivateChatHistory(@PathVariable String lobbyId, @PathVariable String channelName, @RequestParam long userId) {
        return ResponseEntity.ok(communicationService.getPrivateChatHistory(lobbyId, channelName, userId));
    }

    @GetMapping("private/{lobbyId}/channels")
    public ResponseEntity<Object> getPrivateChannels(@PathVariable String lobbyId) {
        return ResponseEntity.ok(communicationService.getPrivateChannels(lobbyId));
    }

}
package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.communication.CommunicationServiceCommunication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class CommunicationController {

    private static final String BULKHEAD_NAME = "gatewayApi";

    private final CommunicationServiceCommunication communicationService;

    @GetMapping("lobby/{lobbyId}")
    @Bulkhead(name = BULKHEAD_NAME)
    @Cacheable(value = "lobbies", key = "#lobbyId")
    public ApiResponse<LobbyDto> getLobby(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.getLobby(lobbyId));
    }

    @PostMapping("lobby/create")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<LobbyDto> createLobby(@RequestBody LobbyCreationDto lobbyCreationDto) {
        return new ApiResponse<>(communicationService.createLobby(lobbyCreationDto));
    }

    @DeleteMapping("lobby/{lobbyId}")
    @Bulkhead(name = BULKHEAD_NAME)
    @CacheEvict(value = {"lobbies", "globalChatHistory", "chatStatus"}, key = "#lobbyId")
    public ApiResponse<DeleteLobbyResponseDto> deleteLobby(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.deleteLobby(lobbyId));
    }

    @PostMapping("global/{lobbyId}/send-message")
    @Bulkhead(name = BULKHEAD_NAME)
    @CacheEvict(value = "globalChatHistory", key = "#lobbyId")
    public ApiResponse<ChatResponse> sendGlobalMessage(@PathVariable String lobbyId, @RequestBody ChatMessage message) {
        return new ApiResponse<>(communicationService.sendGlobalMessage(lobbyId, message));
    }

    @GetMapping("global/{lobbyId}/history")
    @Bulkhead(name = BULKHEAD_NAME)
    @Cacheable(value = "globalChatHistory", key = "#lobbyId")
    public ApiResponse<List<ChatResponse>> getGlobalChatHistory(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.getGlobalChatHistory(lobbyId));
    }

    @PostMapping("global/{lobbyId}/toggle")
    @Bulkhead(name = BULKHEAD_NAME)
    @CacheEvict(value = "chatStatus", key = "#lobbyId")
    public ApiResponse<GlobalChatStatusResponse> toggleGlobalChat(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.toggleGlobalChat(lobbyId));
    }

    @GetMapping("global/{lobbyId}/status")
    @Bulkhead(name = BULKHEAD_NAME)
    @Cacheable(value = "chatStatus", key = "#lobbyId")
    public ApiResponse<GlobalChatStatusResponse> getGlobalChatStatus(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.getGlobalChatStatus(lobbyId));
    }

    @PostMapping("private/{lobbyId}/{channelName}/send-message")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<PrivateChatResponse> sendPrivateMessage(@PathVariable String lobbyId, @PathVariable String channelName, @RequestBody ChatMessage message) {
        return new ApiResponse<>(communicationService.sendPrivateMessage(lobbyId, channelName, message));
    }

    @GetMapping("private/{lobbyId}/{channelName}/history")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<List<PrivateChatResponse>> getPrivateChatHistory(@PathVariable String lobbyId, @PathVariable String channelName, @RequestParam long userId) {
        return new ApiResponse<>(communicationService.getPrivateChatHistory(lobbyId, channelName, userId));
    }

    @GetMapping("private/{lobbyId}/channels")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<List<PrivateChannelDto>> getPrivateChannels(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.getPrivateChannels(lobbyId));
    }

}
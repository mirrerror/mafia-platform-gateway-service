package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.AnnouncementCreationDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.AnnouncementDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatMessage;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.ChatResponse;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.DeleteLobbyResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.GlobalChatStatusResponse;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.LobbyCreationDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.LobbyDto;
import md.faf223.mafiaplatformgatewayservice.dtos.communicationservice.PrivateChatResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.rest_communication.CommunicationServiceCommunication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @CachePut(value = "lobbies", key = "#lobbyCreationDto.lobbyId")
    public ApiResponse<LobbyDto> createLobby(@RequestBody LobbyCreationDto lobbyCreationDto) {
        return new ApiResponse<>(communicationService.createLobby(lobbyCreationDto));
    }

    @DeleteMapping("lobby/{lobbyId}")
    @Bulkhead(name = BULKHEAD_NAME)
    @CacheEvict(value = {"lobbies", "globalChatHistory", "chatStatus", "privateChatHistory", "privateChannels"}, allEntries = true)
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
    @CacheEvict(value = "privateChatHistory", key = "#lobbyId + '_' + #channelName")
    public ApiResponse<PrivateChatResponse> sendPrivateMessage(@PathVariable String lobbyId, @PathVariable String channelName, @RequestBody ChatMessage message) {
        return new ApiResponse<>(communicationService.sendPrivateMessage(lobbyId, channelName, message));
    }

    @GetMapping("private/{lobbyId}/{channelName}/history")
    @Bulkhead(name = BULKHEAD_NAME)
    @Cacheable(value = "privateChatHistory", key = "#lobbyId + '_' + #channelName")
    public ApiResponse<List<PrivateChatResponse>> getPrivateChatHistory(@PathVariable String lobbyId, @PathVariable String channelName, @RequestParam long userId) {
        return new ApiResponse<>(communicationService.getPrivateChatHistory(lobbyId, channelName, userId));
    }

    @GetMapping("private/{lobbyId}/channels")
    @Bulkhead(name = BULKHEAD_NAME)
    @Cacheable(value = "privateChannels", key = "#lobbyId")
    public ApiResponse<List<String>> getPrivateChannels(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.getPrivateChannels(lobbyId));
    }

    @PostMapping("announcement/{lobbyId}")
    @Bulkhead(name = BULKHEAD_NAME)
    @CacheEvict(value = "announcements", key = "#lobbyId")
    public ApiResponse<AnnouncementDto> sendAnnouncement(@PathVariable String lobbyId, @RequestBody AnnouncementCreationDto announcementCreationDto) {
        return new ApiResponse<>(communicationService.sendAnnouncement(lobbyId, announcementCreationDto));
    }

    @GetMapping("announcement/{lobbyId}/history")
    @Bulkhead(name = BULKHEAD_NAME)
    @Cacheable(value = "announcements", key = "#lobbyId")
    public ApiResponse<List<AnnouncementDto>> getAnnouncementHistory(@PathVariable String lobbyId) {
        return new ApiResponse<>(communicationService.getAnnouncementHistory(lobbyId));
    }

}
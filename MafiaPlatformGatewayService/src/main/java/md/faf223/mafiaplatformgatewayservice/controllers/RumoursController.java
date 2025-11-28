package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice.PurchaseRumourDto;
import md.faf223.mafiaplatformgatewayservice.dtos.rumoursservice.Rumour;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.grpc_communication.RumoursServiceGrpcCommunication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/rumours")
@RequiredArgsConstructor
public class RumoursController {

    private static final String BULKHEAD_NAME = "gatewayApi";

    private final RumoursServiceGrpcCommunication rumoursServiceCommunication;

    @PostMapping("/{lobbyId}/purchase")
    @Bulkhead(name = BULKHEAD_NAME)
    @CacheEvict(value = "rumours", key = "#lobbyId + '_' + #purchaseRumourDto.senderId")
    public ApiResponse<Rumour> purchaseRumour(@PathVariable String lobbyId, @RequestBody PurchaseRumourDto purchaseRumourDto) {
        return new ApiResponse<>(rumoursServiceCommunication.purchaseRumour(lobbyId, purchaseRumourDto));
    }

    @GetMapping("/{lobbyId}/user/{ownerId}")
    @Bulkhead(name = BULKHEAD_NAME)
    @Cacheable(value = "rumours", key = "#lobbyId + '_' + #ownerId")
    public ApiResponse<List<Rumour>> getRumours(@PathVariable String lobbyId, @PathVariable long ownerId) {
        return new ApiResponse<>(rumoursServiceCommunication.getRumoursByOwner(lobbyId, ownerId));
    }

}
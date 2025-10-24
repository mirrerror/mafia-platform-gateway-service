package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.AssetDto;
import md.faf223.mafiaplatformgatewayservice.dtos.PlayerAssetsDto;
import md.faf223.mafiaplatformgatewayservice.responses.*;
import md.faf223.mafiaplatformgatewayservice.services.communication.AssetServiceCommunication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/character")
@RequiredArgsConstructor
@Slf4j
public class AssetsController {

    private final AssetServiceCommunication communication;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @GetMapping("/assets/slots")
    @Cacheable(value = "assetSlots", key = "'all'")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<AssetSlotsResponse> getAssetSlots() {
        log.info("Fetching asset slots from Asset Service");
        List<String> slots = communication.getAllAssetSlots();
        return new ApiResponse<>(new AssetSlotsResponse(slots));
    }

    @PostMapping("/{playerId}/assets")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<AssetResponse> addAsset(@PathVariable Long playerId, @RequestBody AssetDto request) {
        AssetResponse asset = communication.addAssetToPlayer(playerId, request);
        return new ApiResponse<>(new AssetResponse(asset.getAssetId(), true));
    }

    @GetMapping("/{playerId}/appearance")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<PlayerAssetsResponse> getPlayerAppearance(@PathVariable String playerId) {
        PlayerAssetsDto assets = communication.getPlayerAppearance(playerId);
        return new ApiResponse<>(new PlayerAssetsResponse(assets));
    }

    @PutMapping("/{playerId}/assets")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<UpdateAssetResponse> changeAsset(@PathVariable Long playerId, @RequestBody AssetDto request) {
        UpdateAssetResponse validTypes = communication.updatePlayerAsset(playerId, request);
        return new ApiResponse<>(validTypes);
    }
}

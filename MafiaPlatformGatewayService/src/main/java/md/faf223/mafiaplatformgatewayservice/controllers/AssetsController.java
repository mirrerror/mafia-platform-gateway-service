package md.faf223.mafiaplatformgatewayservice.controllers;

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

    @GetMapping("/assets/slots")
    @Cacheable(value = "assetSlots", key = "'all'")
    public ApiResponse<AssetSlotsResponse> getAssetSlots() {
        List<String> slots = communication.getAllAssetSlots();
        return new ApiResponse<>(new AssetSlotsResponse(slots));
    }

    @PostMapping("/{playerId}/assets")
    public ApiResponse<AssetResponse> addAsset(@PathVariable Long playerId, @RequestBody AssetDto request) {
        AssetResponse asset = communication.addAssetToPlayer(playerId, request);
        return new ApiResponse<>(new AssetResponse(asset.getAssetId(), true));
    }

    @GetMapping("/{playerId}/appearance")
    public ApiResponse<PlayerAssetsResponse> getPlayerAppearance(@PathVariable String playerId) {
        PlayerAssetsDto assets = communication.getPlayerAppearance(playerId);
        return new ApiResponse<>(new PlayerAssetsResponse(assets));
    }

    @PutMapping("/{playerId}/assets")
    public ApiResponse<UpdateAssetResponse> changeAsset(@PathVariable Long playerId, @RequestBody AssetDto request) {
        UpdateAssetResponse validTypes = communication.updatePlayerAsset(playerId, request);
        return new ApiResponse<>(validTypes);
    }
}

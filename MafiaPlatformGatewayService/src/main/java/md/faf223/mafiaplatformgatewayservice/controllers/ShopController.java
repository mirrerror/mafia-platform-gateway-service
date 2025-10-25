package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.shopservice.PhaseUpdateRequest;
import md.faf223.mafiaplatformgatewayservice.dtos.shopservice.PurchaseRequest;
import md.faf223.mafiaplatformgatewayservice.dtos.shopservice.RumorPurchaseRequest;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.communication.ShopServiceCommunication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopServiceCommunication shopCommunication;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @GetMapping("/items")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<Object> getItems(@RequestParam UUID gameId) {
        // The communication service returns the ApiResponse directly
        return shopCommunication.getItems(gameId);
    }

    @PostMapping("/purchase")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<Object> purchaseItem(@RequestBody PurchaseRequest request) {
        return shopCommunication.purchaseItem(request);
    }

    @PostMapping("/phase-update")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<Object> handlePhaseUpdate(@RequestBody PhaseUpdateRequest request) {
        return shopCommunication.handlePhaseUpdate(request);
    }

    @PostMapping("/rumor-purchase")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<Object> purchaseRumor(@RequestBody RumorPurchaseRequest request) {
        return shopCommunication.purchaseRumor(request);
    }
}
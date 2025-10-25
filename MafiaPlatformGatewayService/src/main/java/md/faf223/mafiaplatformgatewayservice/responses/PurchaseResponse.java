package md.faf223.mafiaplatformgatewayservice.dtos.shopservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PurchaseResponse(
        @JsonProperty("itemId") int itemId,
        @JsonProperty("itemName") String itemName,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("totalCost") int totalCost,
        @JsonProperty("remainingCurrency") RemainingCurrency remainingCurrency
) {
}
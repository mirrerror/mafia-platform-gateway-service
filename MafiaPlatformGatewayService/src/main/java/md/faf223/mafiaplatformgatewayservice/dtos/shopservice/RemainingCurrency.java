package md.faf223.mafiaplatformgatewayservice.dtos.shopservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RemainingCurrency(
        @JsonProperty("coins") int coins,
        @JsonProperty("diamonds") int diamonds
) {
}
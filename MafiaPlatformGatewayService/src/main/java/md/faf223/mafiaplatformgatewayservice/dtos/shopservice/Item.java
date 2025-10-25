package md.faf223.mafiaplatformgatewayservice.dtos.shopservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Item(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") ItemPrice price,
        @JsonProperty("stock") int stock
) {
}
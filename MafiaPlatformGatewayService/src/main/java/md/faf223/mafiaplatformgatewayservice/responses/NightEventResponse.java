package md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record NightEventResponse(
        @JsonProperty("eventId") UUID eventId,
        @JsonProperty("action") String action
) {
}
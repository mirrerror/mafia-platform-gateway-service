package md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record PhaseUpdateResponse(
        @JsonProperty("gameId") UUID gameId,
        @JsonProperty("phase") String phase,
        @JsonProperty("dayNumber") int dayNumber,
        @JsonProperty("announcementsGenerated") int announcementsGenerated
) {
}
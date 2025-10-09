package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminationResponseDto {
    @JsonProperty("data")
    private EliminationData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EliminationData {
        @JsonProperty("gameId")
        private Long gameId;

        @JsonProperty("dayNumber")
        private Integer dayNumber;

        @JsonProperty("votedOutPlayerId")
        private Long votedOutPlayerId;
    }
}

package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateResponseDto {
    @JsonProperty("data")
    private GameStateData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameStateData {
        @JsonProperty("gameId")
        private Long gameId;

        @JsonProperty("phase")
        private String phase;

        @JsonProperty("dayNumber")
        private Integer dayNumber;

        @JsonProperty("playersAlive")
        private List<String> playersAlive;

        @JsonProperty("totalPlayers")
        private Integer totalPlayers;
    }
}

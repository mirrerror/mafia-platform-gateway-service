package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStartResponseDto {
    @JsonProperty("data")
    private GameStartData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameStartData {
        @JsonProperty("gameId")
        private Long gameId;

        @JsonProperty("status")
        private String status;

        @JsonProperty("players")
        private List<PlayerInfo> players;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerInfo {
        @JsonProperty("playerId")
        private Long playerId;

        @JsonProperty("username")
        private String username;
    }
}

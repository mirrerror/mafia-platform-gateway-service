package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayersStatusResponseDto {
    @JsonProperty("data")
    private PlayersStatusData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayersStatusData {
        @JsonProperty("players")
        private List<PlayerStatus> players;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerStatus {
        @JsonProperty("playerId")
        private Long playerId;

        @JsonProperty("username")
        private String username;

        @JsonProperty("status")
        private String status;
    }
}

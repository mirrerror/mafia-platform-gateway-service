package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayersRolesResponseDto {
    @JsonProperty("data")
    private PlayersRolesData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayersRolesData {
        @JsonProperty("players")
        private List<PlayerRole> players;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerRole {
        @JsonProperty("playerId")
        private Long playerId;

        @JsonProperty("username")
        private String username;

        @JsonProperty("role")
        private String role;

        @JsonProperty("career")
        private String career;
    }
}

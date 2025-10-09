package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyJoinResponseDto {
    @JsonProperty("data")
    private LobbyJoinData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LobbyJoinData {
        @JsonProperty("lobbyId")
        private Long lobbyId;

        @JsonProperty("currentPlayers")
        private Integer currentPlayers;

        @JsonProperty("maxPlayers")
        private Integer maxPlayers;
    }
}

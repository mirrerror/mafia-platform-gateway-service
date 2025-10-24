package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyCreateResponseDto {
    @JsonProperty("data")
    private LobbyData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LobbyData {
        @JsonProperty("gameId")
        private Long gameId;

        @JsonProperty("lobbyId")
        private Long lobbyId;

        @JsonProperty("hostId")
        private Long hostId;

        @JsonProperty("status")
        private String status;

        @JsonProperty("joinCode")
        private String joinCode;
    }
}

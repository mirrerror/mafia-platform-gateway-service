package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDto {
    @JsonProperty("data")
    private VoteData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteData {
        @JsonProperty("voteSubmitted")
        private Boolean voteSubmitted;

        @JsonProperty("targetPlayerId")
        private Long targetPlayerId;
    }
}

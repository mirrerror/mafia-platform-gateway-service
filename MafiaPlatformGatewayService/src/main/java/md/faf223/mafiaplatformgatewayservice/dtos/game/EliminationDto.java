package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminationDto {
    @NotNull(message = "Game ID is required")
    @JsonProperty("gameId")
    private Long gameId;

    @NotNull(message = "Day number is required")
    @JsonProperty("dayNumber")
    private Integer dayNumber;

    @NotNull(message = "Voted out player ID is required")
    @JsonProperty("votedOutPlayerId")
    private Long votedOutPlayerId;
}

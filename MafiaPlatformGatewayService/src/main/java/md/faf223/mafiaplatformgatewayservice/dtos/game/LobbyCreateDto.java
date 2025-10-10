package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyCreateDto {
    @NotNull(message = "Host ID is required")
    @JsonProperty("hostId")
    private Long hostId;

    @NotBlank(message = "Lobby name is required")
    @JsonProperty("lobbyName")
    private String lobbyName;

    @NotNull(message = "Max players is required")
    @Min(value = 5, message = "Max players must be at least 5")
    @Max(value = 30, message = "Max players cannot exceed 30")
    @JsonProperty("maxPlayers")
    private Integer maxPlayers;
}

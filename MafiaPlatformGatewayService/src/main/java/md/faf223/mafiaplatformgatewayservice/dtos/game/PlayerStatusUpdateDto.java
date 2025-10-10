package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatusUpdateDto {
    @NotBlank(message = "Status is required")
    @JsonProperty("status")
    private String status;

    @NotBlank(message = "Cause is required")
    @JsonProperty("cause")
    private String cause;

    @NotNull(message = "Day number is required")
    @JsonProperty("dayNumber")
    private Integer dayNumber;
}

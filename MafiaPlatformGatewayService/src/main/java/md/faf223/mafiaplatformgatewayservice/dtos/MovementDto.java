package md.faf223.mafiaplatformgatewayservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementDto {
    private Long playerId;
    private Long locationId;
    private String timestamp;
}

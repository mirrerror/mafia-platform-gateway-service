package md.faf223.mafiaplatformgatewayservice.dtos.tasks;

import lombok.Data;

@Data
public class MovementEventBody {
    private Long gameId;
    private Long playerId;
    private Long locationId;
}
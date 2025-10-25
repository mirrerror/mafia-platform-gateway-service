package md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice;

import lombok.Data;
import java.util.UUID;

@Data
public class NightEventRequest {
    private UUID gameId;
    private Integer playerId;
    private String action;
    private Integer targetPlayerId;
}
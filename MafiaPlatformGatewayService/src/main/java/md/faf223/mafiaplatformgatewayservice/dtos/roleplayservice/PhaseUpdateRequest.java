package md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice;

import lombok.Data;
import java.util.UUID;

@Data
public class PhaseUpdateRequest {
    private UUID gameId;
    private String newPhase;
    private Integer dayNumber;
}
package md.faf223.mafiaplatformgatewayservice.dtos.shopservice;

import lombok.Data;
import java.util.UUID;

@Data
public class PhaseUpdateRequest {
    private UUID gameId;
    private String newPhase;
    private Integer dayNumber;
}
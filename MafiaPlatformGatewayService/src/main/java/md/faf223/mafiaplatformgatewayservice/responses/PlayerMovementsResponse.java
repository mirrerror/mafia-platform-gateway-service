package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.LocationVisitedDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerMovementsResponse {
    private Long playerId;
    private List<LocationVisitedDto> movements;
}

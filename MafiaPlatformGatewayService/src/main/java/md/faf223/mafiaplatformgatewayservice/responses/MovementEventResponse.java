package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.Data;

import java.util.List;

@Data
public class MovementEventResponse {
    private long gameId;
    private long playerId;
    private long locationId;
    private String locationName;
    private List<Long> updatedTaskIds;
    private String result;
}

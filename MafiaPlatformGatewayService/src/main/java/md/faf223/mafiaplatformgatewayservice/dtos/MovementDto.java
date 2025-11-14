package md.faf223.mafiaplatformgatewayservice.dtos;

import com.dmitrii.townservice.grpc.TownServiceProto;
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

    public MovementDto(TownServiceProto.Movement movement) {
        this.playerId = movement.getPlayerId();
        this.locationId = movement.getLocationId();
        this.timestamp = movement.getTimestamp();
    }
}

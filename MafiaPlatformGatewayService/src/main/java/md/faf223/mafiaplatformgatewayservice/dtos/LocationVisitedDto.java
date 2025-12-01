package md.faf223.mafiaplatformgatewayservice.dtos;

import com.dmitrii.townservice.grpc.TownServiceProto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationVisitedDto {
    private Long locationId;
    private String locationName;
    private String timestamp;

    public LocationVisitedDto(TownServiceProto.LocationVisited locationVisited) {
        this.locationId = locationVisited.getLocationId();
        this.locationName = locationVisited.getName();
        this.timestamp = locationVisited.getTimestamp();
    }
}

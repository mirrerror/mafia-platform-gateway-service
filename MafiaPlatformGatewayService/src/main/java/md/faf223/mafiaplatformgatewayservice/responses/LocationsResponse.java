package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.LocationDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationsResponse {
    private List<LocationDto> locations;
}

package md.faf223.mafiaplatformgatewayservice.dtos;

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
}

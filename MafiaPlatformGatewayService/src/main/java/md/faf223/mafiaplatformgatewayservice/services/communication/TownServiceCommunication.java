package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.LocationDto;
import md.faf223.mafiaplatformgatewayservice.responses.LocationsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TownServiceCommunication extends BaseCommunication {

    public TownServiceCommunication(@Value("${TOWN_SERVICE_HOST}") String baseUrl,
                                    @Value("${TOWN_SERVICE_PORT}") String port) {
        super(baseUrl, port, "TownCommunication");
    }

    public List<LocationDto> getAllLocations() {
        LocationsResponse response = makeGetRequest(
                "/locations",
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getLocations();
    }

    public LocationDto getLocationById(String locationId) {
        return makeGetRequest(
                String.format("/locations/%s", locationId),
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
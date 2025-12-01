package md.faf223.mafiaplatformgatewayservice.services.rest_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.LocationDto;
import md.faf223.mafiaplatformgatewayservice.responses.LocationsResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TownServiceCommunication extends BaseRestCommunication {

    public TownServiceCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("town-service", discoveryServiceClient);
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
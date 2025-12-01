package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.LocationDto;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.LocationsResponse;
import md.faf223.mafiaplatformgatewayservice.services.grpc_communication.TownServiceGrpcCommunication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/town/locations")
public class TownController {

    private final TownServiceGrpcCommunication townCommunication;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @GetMapping
    @Cacheable(value = "locations", key = "'all'")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<LocationsResponse> getAllLocations() {
        List<LocationDto> locations = townCommunication.getAllLocations();
        return new ApiResponse<>(new LocationsResponse(locations));
    }

    @GetMapping("/{id}")
    @Cacheable(value = "locations", key = "#id")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<LocationDto> getLocationById(@PathVariable String id) {
        LocationDto location = townCommunication.getLocationById(id);
        return new ApiResponse<>(location);
    }
}

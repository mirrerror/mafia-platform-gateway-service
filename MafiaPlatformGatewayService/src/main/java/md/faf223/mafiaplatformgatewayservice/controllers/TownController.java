package md.faf223.mafiaplatformgatewayservice.controllers;

import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.LocationDto;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.LocationsResponse;
import md.faf223.mafiaplatformgatewayservice.services.communication.TownCommunication;
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

    private final TownCommunication townCommunication;

    @GetMapping
    @Cacheable(value = "locations", key = "'all'")
    public ApiResponse<LocationsResponse> getAllLocations() {
        List<LocationDto> locations = townCommunication.getAllLocations();
        return new ApiResponse<>(new LocationsResponse(locations));
    }

    @GetMapping("/{id}")
    @Cacheable(value = "locations", key = "#id")
    public ApiResponse<LocationDto> getLocationById(@PathVariable String id) {
        LocationDto location = townCommunication.getLocationById(id);
        return new ApiResponse<>(location);
    }
}

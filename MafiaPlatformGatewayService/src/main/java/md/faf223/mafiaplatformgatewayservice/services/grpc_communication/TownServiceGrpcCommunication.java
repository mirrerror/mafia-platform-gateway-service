package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import com.dmitrii.townservice.grpc.LocationServiceGrpc;
import com.dmitrii.townservice.grpc.TownServiceProto;
import md.faf223.mafiaplatformgatewayservice.dtos.LocationDto;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TownServiceGrpcCommunication extends BaseGrpcCommunication {

    public TownServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("town-service", discoveryServiceClient);
    }

    public List<LocationDto> getAllLocations() {
        TownServiceProto.GetAllLocationsResponse grpcResponse = executeGrpcCall(channel -> {
            // 1. Create a client
            LocationServiceGrpc.LocationServiceBlockingStub stub =
                    LocationServiceGrpc.newBlockingStub(channel);

            // 2. Create the gRPC request
            TownServiceProto.GetAllLocationsRequest grpcRequest = TownServiceProto.GetAllLocationsRequest.newBuilder().build();

            // 3. Make a call
            return stub.getAllLocations(grpcRequest);
        });

        // 4. Convert gRPC Response back to the internal DTO
        return grpcResponse.getLocationsList().stream()
                .map(this::toLocationDto)
                .collect(Collectors.toList());
    }

    public LocationDto getLocationById(String locationId) {
        TownServiceProto.GetLocationByIdResponse grpcResponse = executeGrpcCall(channel -> {
            LocationServiceGrpc.LocationServiceBlockingStub stub =
                    LocationServiceGrpc.newBlockingStub(channel);

            TownServiceProto.GetLocationByIdRequest grpcRequest = TownServiceProto.GetLocationByIdRequest.newBuilder()
                    .setLocationId(Long.parseLong(locationId))
                    .build();

            return stub.getLocationById(grpcRequest);
        });

        return toLocationDto(grpcResponse.getLocation());
    }

    private LocationDto toLocationDto(TownServiceProto.Location grpcLocation) {
        return new LocationDto(
                grpcLocation.getId(),
                grpcLocation.getName(),
                grpcLocation.getDescription()
        );
    }
}

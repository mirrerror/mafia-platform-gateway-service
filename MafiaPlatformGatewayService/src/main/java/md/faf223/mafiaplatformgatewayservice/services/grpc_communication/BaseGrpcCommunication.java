package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.ServiceInstance;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import md.faf223.mafiaplatformgatewayservice.services.ServiceCircuitBreaker;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public abstract class BaseGrpcCommunication {

    private final String serviceName;
    private final DiscoveryServiceClient discoveryServiceClient;
    private final ServiceCircuitBreaker circuitBreaker;

    public BaseGrpcCommunication(String serviceName, DiscoveryServiceClient discoveryServiceClient) {
        this.serviceName = serviceName;
        this.discoveryServiceClient = discoveryServiceClient;
        this.circuitBreaker = new ServiceCircuitBreaker(serviceName, discoveryServiceClient);
    }

    protected <T> T executeGrpcCall(Function<ManagedChannel, T> logic) {
        ServiceInstance instance = discoveryServiceClient.getServiceInstance(serviceName);
        String target = instance.getRPCUrl();

        log.info("Making a grpc call to {}", target);

        // Create the gRPC channel
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        try {
            return logic.apply(channel);
        } catch (StatusRuntimeException e) {
            circuitBreaker.recordFailure(instance);
            throw translateGrpcException(e);
        } finally {
            try {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private MicroserviceException translateGrpcException(StatusRuntimeException e) {
        log.error("gRPC call failed to {}: {}", serviceName, e.getStatus());
        int httpStatus = switch (e.getStatus().getCode()) {
            case DEADLINE_EXCEEDED -> 504; // Gateway Timeout
            case UNAVAILABLE -> 503; // Service Unavailable
            case NOT_FOUND -> 404; // Not Found
            case INVALID_ARGUMENT -> 400; // Bad Request
            default -> 500; // Internal Server Error
        };

        String errorBody = String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                e.getStatus().getCode(), e.getStatus().getDescription());
        return new MicroserviceException(httpStatus, errorBody);
    }
}

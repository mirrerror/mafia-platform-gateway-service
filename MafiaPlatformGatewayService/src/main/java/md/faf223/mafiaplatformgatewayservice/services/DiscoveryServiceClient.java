package md.faf223.mafiaplatformgatewayservice.services;

import com.pad.lab.servicediscovery.grpc.DiscoveryQueryServiceGrpc;
import com.pad.lab.servicediscovery.grpc.RegistrationProto;
import com.pad.lab.servicediscovery.grpc.RegistrationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DiscoveryServiceClient {

    @Value("${DISCOVERY_SERVICE_GRPC_URL}")
    private String discoveryServiceGrpcUrl;

    public ServiceInstance getServiceInstance(String serviceName) {
        log.info("gRPC: Fetching best instance for {}", serviceName);
        ManagedChannel channel = null;
        try {
            RegistrationProto.GetBestInstanceRequest request = RegistrationProto.GetBestInstanceRequest.newBuilder()
                    .setServiceId(serviceName)
                    .build();

            channel = ManagedChannelBuilder.forTarget(discoveryServiceGrpcUrl)
                    .usePlaintext()
                    .build();

            DiscoveryQueryServiceGrpc.DiscoveryQueryServiceBlockingStub stub =
                    DiscoveryQueryServiceGrpc.newBlockingStub(channel);

            RegistrationProto.ServiceInstanceMessage grpcResponse = stub.getBestInstance(request);

            ServiceInstance instance = new ServiceInstance();
            instance.setInstanceId(grpcResponse.getInstanceId());
            instance.setServiceId(grpcResponse.getServiceId());
            instance.setHost(grpcResponse.getHost());
            instance.setRestPort(grpcResponse.getRestPort());
            instance.setRpcPort(grpcResponse.getRpcPort());
            instance.setTopicName(grpcResponse.getTopicName());

            log.info("Selected instance for {}: {}", serviceName, instance.getRPCUrl());
            return instance;

        } catch (StatusRuntimeException e) {
            log.error("gRPC Error fetching service instance for {}: {}", serviceName, e.getStatus());
            throw new RuntimeException("Failed to discover service: " + serviceName, e);
        } catch (Exception e) {
            log.error("Error fetching service instance for {}: {}", serviceName, e.getMessage());
            throw new RuntimeException("Failed to discover service: " + serviceName, e);
        } finally {
            if (channel != null) {
                try {
                    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.warn("Failed to shut down gRPC channel to discovery service", e);
                }
            }
        }
    }

    public void deregisterServiceInstance(String instanceId) {
        log.warn("gRPC: Deregistering service instance: {}", instanceId);
        ManagedChannel channel = null;
        try {
            RegistrationProto.DeregisterRequest request = RegistrationProto.DeregisterRequest.newBuilder()
                    .setInstanceId(instanceId)
                    .build();

            channel = ManagedChannelBuilder.forTarget(discoveryServiceGrpcUrl)
                    .usePlaintext()
                    .build();

            RegistrationServiceGrpc.RegistrationServiceBlockingStub stub =
                    RegistrationServiceGrpc.newBlockingStub(channel);

            stub.deregister(request);

            log.info("Successfully deregistered service instance: {}", instanceId);
        } catch (StatusRuntimeException e) {
            log.error("gRPC Error deregistering service instance {}: {}", instanceId, e.getStatus());
            throw new RuntimeException("Failed to deregister service: " + instanceId, e);
        } catch (Exception e) {
            log.error("Error deregistering service instance {}: {}", instanceId, e.getMessage());
            throw new RuntimeException("Failed to deregister service: " + instanceId, e);
        } finally {
            if (channel != null) {
                try {
                    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.warn("Failed to shut down gRPC channel to discovery service", e);
                }
            }
        }
    }
}
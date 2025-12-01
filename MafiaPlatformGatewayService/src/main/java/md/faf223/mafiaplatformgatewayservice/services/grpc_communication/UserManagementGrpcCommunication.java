package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import com.pad.lab.grpc.UserServiceGrpc;
import com.pad.lab.grpc.UserServiceProto;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.LoginUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.RegisterUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.*;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import md.faf223.mafiaplatformgatewayservice.services.UserManagementCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserManagementGrpcCommunication extends BaseGrpcCommunication {

    private final UserManagementCacheService cacheService;

    public UserManagementGrpcCommunication(
            DiscoveryServiceClient discoveryServiceClient,
            @Autowired(required = false) UserManagementCacheService cacheService) {
        super("user-management-service", discoveryServiceClient);
        this.cacheService = cacheService;
    }

    /**
     * Authenticate user and get JWT token
     */
    public UserManagementLoginResponseDto login(LoginUserDto loginDto) {
        UserServiceProto.LoginResponse grpcResponse = executeGrpcCall(channel -> {
            UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

            UserServiceProto.LoginRequest request = UserServiceProto.LoginRequest.newBuilder()
                    .setUsername(loginDto.getUsername())
                    .setPassword(loginDto.getPassword())
                    .build();

            return stub.login(request);
        });

        // Check for error
        if (grpcResponse.hasError()) {
            UserServiceProto.Error error = grpcResponse.getError();
            throw new MicroserviceException(401, 
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}", 
                            error.getCode(), error.getMessage()));
        }

        // Convert to DTO - now uses nested data message
        UserServiceProto.LoginData data = grpcResponse.getData();
        UserManagementLoginResponseDto.LoginData loginData = 
                new UserManagementLoginResponseDto.LoginData(data.getToken(), data.getUsername());
        return new UserManagementLoginResponseDto(loginData);
    }

    /**
     * Register a new user
     */
    public UserManagementRegisterResponseDto register(RegisterUserDto registerDto) {
        UserServiceProto.RegisterResponse grpcResponse = executeGrpcCall(channel -> {
            UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

            UserServiceProto.RegisterRequest request = UserServiceProto.RegisterRequest.newBuilder()
                    .setUsername(registerDto.getUsername())
                    .setPassword(registerDto.getPassword())
                    .setEmail(registerDto.getEmail())
                    .build();

            return stub.register(request);
        });

        // Check for error
        if (grpcResponse.hasError()) {
            UserServiceProto.Error error = grpcResponse.getError();
            throw new MicroserviceException(400, 
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}", 
                            error.getCode(), error.getMessage()));
        }

        // Convert to DTO - now uses nested data message
        UserServiceProto.RegisterData data = grpcResponse.getData();
        UserManagementRegisterResponseDto.RegisterData registerData = 
                new UserManagementRegisterResponseDto.RegisterData(data.getId(), data.getUsername());
        return new UserManagementRegisterResponseDto(registerData);
    }

    /**
     * Get user profile by ID
     */
    public UserProfileResponseDto getProfile(Long userId, String username) {
        // Try to get from cache first
        if (cacheService != null) {
            UserProfileResponseDto cached = cacheService.getCachedUserProfile(userId);
            if (cached != null) {
                log.info("Cache hit for user profile: {}", userId);
                return cached;
            }
        }

        log.info("Fetching profile for userId={}", userId);

        UserServiceProto.GetProfileResponse grpcResponse = executeGrpcCall(channel -> {
            UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

            UserServiceProto.GetProfileRequest request = UserServiceProto.GetProfileRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            return stub.getProfile(request);
        });

        // Check for error
        if (grpcResponse.hasError()) {
            UserServiceProto.Error error = grpcResponse.getError();
            throw new MicroserviceException(404, 
                    String.format("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}", 
                            error.getCode(), error.getMessage()));
        }

        // Convert to DTO - now uses nested data message with Currency object
        UserServiceProto.GetProfileData data = grpcResponse.getData();
        Map<String, Integer> currencyMap = new HashMap<>();
        if (data.hasCurrency()) {
            UserServiceProto.Currency currency = data.getCurrency();
            currencyMap.put("diamonds", (int) currency.getDiamonds());
            currencyMap.put("coins", (int) currency.getCoins());
        }

        UserProfileResponseDto.UserProfileData profileData = new UserProfileResponseDto.UserProfileData(
                data.getId(),
                data.getUsername(),
                data.getEmail(),
                currencyMap
        );
        UserProfileResponseDto profile = new UserProfileResponseDto(profileData);

        // Cache the result
        if (cacheService != null) {
            return cacheService.cacheUserProfile(userId, profile);
        }

        return profile;
    }

    /**
     * Validate JWT token with the User Management Service
     */
    public ValidateTokenResponseDto validateToken(String token) {
        UserServiceProto.ValidateTokenResponse grpcResponse = executeGrpcCall(channel -> {
            UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

            UserServiceProto.ValidateTokenRequest request = UserServiceProto.ValidateTokenRequest.newBuilder()
                    .setToken(token)
                    .build();

            return stub.validateToken(request);
        });

        return new ValidateTokenResponseDto(
                grpcResponse.getValid(),
                grpcResponse.getUserId(),
                grpcResponse.getUsername()
        );
    }
}

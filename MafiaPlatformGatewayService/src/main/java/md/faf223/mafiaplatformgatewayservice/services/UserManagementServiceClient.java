package md.faf223.mafiaplatformgatewayservice.services;

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.LoginUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.RegisterUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.*;
import md.faf223.mafiaplatformgatewayservice.services.communication.BaseCommunication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserManagementServiceClient extends BaseCommunication {

    private final UserManagementCacheService cacheService;

    public UserManagementServiceClient(
            DiscoveryServiceClient discoveryServiceClient,
            @Autowired(required = false) UserManagementCacheService cacheService) {
        super("user-management-service", discoveryServiceClient);
        this.cacheService = cacheService;
    }

    public UserManagementRegisterResponseDto register(RegisterUserDto registerDto) {
        return makePostRequest(
                "/register",
                registerDto,
                new ParameterizedTypeReference<>() {}
        );
    }

    public UserManagementLoginResponseDto login(LoginUserDto loginDto) {
        return makePostRequest(
                "/login",
                loginDto,
                new ParameterizedTypeReference<>() {}
        );
    }

    public UserProfileResponseDto getProfile(Long userId, String token) {
        // Try to get from cache first
        if (cacheService != null) {
            UserProfileResponseDto cached = cacheService.getCachedUserProfile(userId);
            if (cached != null) {
                log.info("Cache hit for user profile: {}", userId);
                return cached;
            }
        }

        UserProfileResponseDto profile = makeGetRequest(
                "/profile/" + userId,
                new ParameterizedTypeReference<>() {}
        );

        // Cache the result
        if (cacheService != null && profile != null) {
            return cacheService.cacheUserProfile(userId, profile);
        }

        return profile;
    }

    public CurrencyUpdateResponseDto updateCurrency(Long userId, UpdateCurrencyDto updateDto, String token) {
        CurrencyUpdateResponseDto response = makePutRequest(
                "/currency/" + userId,
                updateDto,
                new ParameterizedTypeReference<>() {}
        );

        // Invalidate user profile cache since currency changed
        if (cacheService != null) {
            cacheService.evictUserProfileOnCurrencyUpdate(userId);
        }

        return response;
    }
}
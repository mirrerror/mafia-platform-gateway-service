package md.faf223.mafiaplatformgatewayservice.services;

import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.LoginUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.RegisterUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.*;
import md.faf223.mafiaplatformgatewayservice.services.communication.BaseCommunication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Get user profile by ID with authentication headers.
     * 
     * The User Management Service expects X-User-Id and X-Username headers
     * for authentication. The gateway validates the JWT token and forwards
     * the user information via these headers.
     *
     * @param userId The ID of the user profile to retrieve
     * @param username The username extracted from the validated JWT token
     * @return The user profile data
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

        // Build authentication headers for User Management Service
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", String.valueOf(userId));
        headers.put("X-Username", username);

        log.info("Fetching profile for userId={}, username={}", userId, username);

        // Use the new overloaded method with headers support
        UserProfileResponseDto profile = makeGetRequest(
                "/profile/" + userId,
                headers,
                new ParameterizedTypeReference<>() {}
        );

        // Cache the result
        if (cacheService != null && profile != null) {
            return cacheService.cacheUserProfile(userId, profile);
        }

        return profile;
    }

    /**
     * Update user currency.
     * 
     * This endpoint is internal and does not require authentication headers.
     * It's called by other services (Game Service, Rumours Service, etc.)
     * for internal currency operations.
     *
     * @param userId The ID of the user whose currency to update
     * @param updateDto The currency update details (operation, amount, currency type)
     * @return The currency update response with new balance
     */
    public CurrencyUpdateResponseDto updateCurrency(Long userId, UpdateCurrencyDto updateDto) {
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
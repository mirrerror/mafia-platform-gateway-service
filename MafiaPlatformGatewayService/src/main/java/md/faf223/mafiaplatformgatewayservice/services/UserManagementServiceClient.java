package md.faf223.mafiaplatformgatewayservice.services;

import md.faf223.mafiaplatformgatewayservice.dtos.LoginUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.RegisterUserDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserManagementServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementServiceClient.class);
    private final RestTemplate authRestTemplate; // For all User Management Service endpoints
    private final UserManagementCacheService cacheService;
    private final String userManagementServiceUrl;

    @Value("${services.user-management.host}")
    private String userManagementHost;

    @Value("${services.user-management.port}")
    private String userManagementPort;

    // Use authRestTemplate which forwards the Authorization header
    public UserManagementServiceClient(
            @Qualifier("userManagementRestTemplate") RestTemplate authRestTemplate,
            UserManagementCacheService cacheService,
            @Value("${services.user-management.host}") String host,
            @Value("${services.user-management.port}") String port) {
        this.authRestTemplate = authRestTemplate;
        this.cacheService = cacheService;
        this.userManagementServiceUrl = "http://" + host + ":" + port;
    }

    public UserManagementRegisterResponseDto register(RegisterUserDto registerDto) {
        String url = userManagementServiceUrl + "/register";
        
        try {
            ResponseEntity<UserManagementRegisterResponseDto> response = authRestTemplate.postForEntity(
                url, 
                registerDto, 
                UserManagementRegisterResponseDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error registering user: {}", ex.getMessage());
            throw ex;
        }
    }

    public UserManagementLoginResponseDto login(LoginUserDto loginDto) {
        String url = userManagementServiceUrl + "/login";
        
        try {
            ResponseEntity<UserManagementLoginResponseDto> response = authRestTemplate.postForEntity(
                url, 
                loginDto, 
                UserManagementLoginResponseDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error logging in user: {}", ex.getMessage());
            throw ex;
        }
    }

    public UserProfileResponseDto getProfile(Long userId, String token) {
        // Try to get from cache first
        UserProfileResponseDto cached = cacheService.getCachedUserProfile(userId);
        if (cached != null) {
            logger.info("Cache hit for user profile: {}", userId);
            return cached;
        }
        
        String url = userManagementServiceUrl + "/profile/" + userId;
        
        // Extract username from token to send as header
        String username = null;
        if (token != null) {
            try {
                username = extractUsernameFromToken(token);
            } catch (Exception e) {
                logger.warn("Could not extract username from token: {}", e.getMessage());
            }
        }
        
        // Send user info as headers (Gateway has already validated the token)
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", String.valueOf(userId));
        if (username != null) {
            headers.set("X-Username", username);
        }
        
        logger.info("Sending request to {} with headers: X-User-Id={}, X-Username={}", url, userId, username);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<UserProfileResponseDto> response = authRestTemplate.exchange(
                url, 
                HttpMethod.GET,
                entity,
                UserProfileResponseDto.class
            );
            
            // Cache the result
            UserProfileResponseDto result = response.getBody();
            cacheService.cacheUserProfile(userId, result);
            
            return result;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error getting user profile: {}", ex.getMessage());
            throw ex;
        }
    }

    public CurrencyUpdateResponseDto updateCurrency(Long userId, UpdateCurrencyDto updateDto, String token) {
        String url = userManagementServiceUrl + "/currency/" + userId;
        
        // Internal endpoint - no authentication headers required
        // User Management Service has been updated to not require X-User-Id and X-Username headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateCurrencyDto> entity = new HttpEntity<>(updateDto, headers);
        
        try {
            ResponseEntity<CurrencyUpdateResponseDto> response = authRestTemplate.exchange(
                url, 
                HttpMethod.PUT, 
                entity, 
                CurrencyUpdateResponseDto.class
            );
            
            // Invalidate user profile cache since currency changed
            cacheService.evictUserProfileOnCurrencyUpdate(userId);
            
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            logger.error("Error updating currency: {}", ex.getMessage());
            throw ex;
        }
    }
    
    private String extractUsernameFromToken(String token) {
        // Simple JWT payload extraction without full validation
        // Token format: header.payload.signature
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        
        try {
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            // Parse JSON manually to extract "sub" field
            int subIndex = payload.indexOf("\"sub\":\"");
            if (subIndex != -1) {
                int startIndex = subIndex + 7; // length of "\"sub\":\""
                int endIndex = payload.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    return payload.substring(startIndex, endIndex);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
        }
        return null;
    }
}

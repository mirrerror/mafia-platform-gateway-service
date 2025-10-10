package md.faf223.mafiaplatformgatewayservice.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import org.springframework.stereotype.Component;

/**
 * Utility class to extract authenticated user information from JWT tokens
 * Use this in controllers to get the current user's ID and username
 */
@Component
@RequiredArgsConstructor
public class JwtTokenExtractor {

    private final JwtService jwtService;

    /**
     * Extract authenticated user from HTTP request
     * @param request the HTTP request
     * @return AuthenticatedUser containing userId and username, or null if token is invalid
     */
    public AuthenticatedUser extractUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            
            if (username == null || userId == null) {
                return null;
            }

            return new AuthenticatedUser(userId, username);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract just the JWT token from the request
     * @param request the HTTP request
     * @return the JWT token string, or null if not present
     */
    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }
}

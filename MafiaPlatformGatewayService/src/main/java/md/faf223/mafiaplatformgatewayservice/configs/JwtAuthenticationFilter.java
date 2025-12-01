package md.faf223.mafiaplatformgatewayservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.ErrorResponseDto;
import md.faf223.mafiaplatformgatewayservice.exceptions.InvalidJWTTokenException;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String requestPath = request.getRequestURI();
        final String requestMethod = request.getMethod();

        logger.info("JWT Filter - Path: {}, Method: {}", requestPath, requestMethod);

        // Skip JWT validation for public endpoints
        if (requestPath.startsWith("/api/auth/") || 
            requestPath.startsWith("/api/ws/") || 
            requestPath.startsWith("/api/chat/") || 
            requestPath.startsWith("/api/rumours/")) {
            logger.info("Skipping JWT validation - public endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Skip JWT validation for internal Game Service endpoints
        if (isInternalGameEndpoint(requestPath, requestMethod)) {
            logger.info("Skipping JWT validation - internal game endpoint: {} {}", requestMethod, requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);
            final Long userId = jwtService.extractUserId(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && userId != null && authentication == null) {
                // Validate token signature and expiration
                if (!jwtService.isTokenExpired(jwt)) {
                    // Create authentication token without loading user from database
                    // The User Management Service already validated the user when creating the token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new InvalidJWTTokenException("Token has expired");
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            logger.error("JWT token expired: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Token has expired. Please login again.");
        } catch (MalformedJwtException ex) {
            logger.error("Malformed JWT token: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token format.");
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token signature.");
        } catch (InvalidJWTTokenException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", ex.getMessage());
        } catch (Exception exception) {
            logger.error("Exception in JWT filter: {}", exception.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid or expired token.");
        }
    }

    /**
     * Send a JSON error response
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String code, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                new ErrorResponseDto.ErrorDetail(code, message)
        );
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    /**
     * Check if the request is for an internal Game Service endpoint that doesn't require JWT
     */
    private boolean isInternalGameEndpoint(String requestPath, String requestMethod) {
        // GET /api/game/{game_id}/state
        if ("GET".equals(requestMethod) && requestPath.matches("/api/game/\\d+/state")) {
            return true;
        }
        
        // GET /api/game/{game_id}/players/status
        if ("GET".equals(requestMethod) && requestPath.matches("/api/game/\\d+/players/status")) {
            return true;
        }
        
        // PUT /api/game/{game_id}/players/{player_id}/status
        if ("PUT".equals(requestMethod) && requestPath.matches("/api/game/\\d+/players/\\d+/status")) {
            return true;
        }
        
        // GET /api/game/{game_id}/events
        if ("GET".equals(requestMethod) && requestPath.matches("/api/game/\\d+/events")) {
            return true;
        }
        
        // GET /api/game/{game_id}/players-roles
        if ("GET".equals(requestMethod) && requestPath.matches("/api/game/\\d+/players-roles")) {
            return true;
        }
        
        // POST /api/game/{game_id}/voting/elimination
        if ("POST".equals(requestMethod) && requestPath.matches("/api/game/\\d+/voting/elimination")) {
            return true;
        }
        
        return false;
    }

}

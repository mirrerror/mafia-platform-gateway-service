package md.faf223.mafiaplatformgatewayservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.*;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import md.faf223.mafiaplatformgatewayservice.services.UserManagementServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
    private final UserManagementServiceClient userManagementService;
    private final JwtService jwtService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        try {
            // Extract token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            String token = authHeader.substring(7);
            
            // Validate token
            String username = jwtService.extractUsername(token);
            Long userIdFromToken = jwtService.extractUserId(token);
            
            if (username == null || userIdFromToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            // Check if the user is accessing their own profile
            if (!userIdFromToken.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "FORBIDDEN", 
                        "Not authorized to access this profile"
                    )));
            }

            // Forward the username to User Management Service via headers
            // The service expects X-User-Id and X-Username for authentication
            UserProfileResponseDto profile = userManagementService.getProfile(id, username);
            return ResponseEntity.ok(profile);
            
        } catch (HttpClientErrorException ex) {
            logger.error("Client error: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            logger.error("Server error: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                    "INTERNAL_ERROR", 
                    "An unexpected error occurred"
                )));
        }
    }

    @PutMapping("/currency/{id}")
    public ResponseEntity<?> updateCurrency(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCurrencyDto updateDto
    ) {
        try {
            // Internal endpoint - no JWT validation required
            // This endpoint is called by other services (Game Service, Rumours Service, etc.)
            // User Management Service does not require authentication headers for this endpoint
            CurrencyUpdateResponseDto response = userManagementService.updateCurrency(id, updateDto);
            return ResponseEntity.ok(response);
            
        } catch (HttpClientErrorException ex) {
            logger.error("Client error: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            logger.error("Server error: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                    "INTERNAL_ERROR", 
                    "An unexpected error occurred"
                )));
        }
    }
}

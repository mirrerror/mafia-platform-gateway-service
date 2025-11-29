package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.*;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import md.faf223.mafiaplatformgatewayservice.services.grpc_communication.UserManagementGrpcCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
    private final UserManagementGrpcCommunication userManagementService;
    private final JwtService jwtService;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @GetMapping("/profile/{id}")
    @Bulkhead(name = BULKHEAD_NAME)
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
            
            // Validate token locally (shared secret key)
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

            // Get profile via gRPC
            UserProfileResponseDto profile = userManagementService.getProfile(id, username);
            return ResponseEntity.ok(profile);
            
        } catch (MicroserviceException ex) {
            logger.error("Error fetching profile: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getErrorBody());
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

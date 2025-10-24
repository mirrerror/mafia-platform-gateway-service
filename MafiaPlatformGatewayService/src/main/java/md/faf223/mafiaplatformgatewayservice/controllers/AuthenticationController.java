package md.faf223.mafiaplatformgatewayservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.*;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.ErrorResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.UserManagementLoginResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.UserManagementRegisterResponseDto;
import md.faf223.mafiaplatformgatewayservice.models.User;
import md.faf223.mafiaplatformgatewayservice.responses.LoginResponse;
import md.faf223.mafiaplatformgatewayservice.services.AuthenticationService;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import md.faf223.mafiaplatformgatewayservice.services.UserManagementServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserManagementServiceClient userManagementService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        try {
            UserManagementRegisterResponseDto response = userManagementService.register(registerUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (HttpClientErrorException ex) {
            logger.error("Error during registration: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            logger.error("Server error during registration: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error during registration: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                    "INTERNAL_ERROR", 
                    "An unexpected error occurred"
                )));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        try {
            // Call user management service to authenticate and get token
            UserManagementLoginResponseDto response = userManagementService.login(loginUserDto);
            
            // The user management service already returns a token with username and user_id
            // We can either use that token directly or generate a new one at the gateway level
            // For consistency with Python service, we'll use the token from the service
            
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            logger.error("Error during login: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            logger.error("Server error during login: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error during login: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                    "INTERNAL_ERROR", 
                    "An unexpected error occurred"
                )));
        }
    }

}
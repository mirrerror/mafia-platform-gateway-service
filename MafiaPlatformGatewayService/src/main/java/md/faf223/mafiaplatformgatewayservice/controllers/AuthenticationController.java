package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.*;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.ErrorResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.UserManagementLoginResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.UserManagementRegisterResponseDto;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.services.grpc_communication.UserManagementGrpcCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final UserManagementGrpcCommunication userManagementService;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @PostMapping("/signup")
    @Bulkhead(name = BULKHEAD_NAME)
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        try {
            UserManagementRegisterResponseDto response = userManagementService.register(registerUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (MicroserviceException ex) {
            logger.error("Error during registration: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getErrorBody());
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
    @Bulkhead(name = BULKHEAD_NAME)
    public ResponseEntity<?> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        try {
            // Call user management service via gRPC to authenticate and get token
            UserManagementLoginResponseDto response = userManagementService.login(loginUserDto);
            return ResponseEntity.ok(response);
        } catch (MicroserviceException ex) {
            logger.error("Error during login: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getErrorBody());
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
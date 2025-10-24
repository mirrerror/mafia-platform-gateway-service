package md.faf223.mafiaplatformgatewayservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.CurrencyUpdateResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.ErrorResponseDto;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.UpdateCurrencyDto;
import md.faf223.mafiaplatformgatewayservice.services.UserManagementServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 * Legacy endpoint controller for backward compatibility with rumours service.
 * This handles the old /currency/{id} path that rumours service is calling.
 * Maps to the same functionality as /api/users/currency/{id}
 */
@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);
    private final UserManagementServiceClient userManagementService;

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCurrency(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCurrencyDto updateDto
    ) {
        try {
            logger.info("Legacy currency endpoint called for user ID: {} with operation: {} {} {}", 
                       id, updateDto.getOperation(), updateDto.getAmount(), updateDto.getCurrency());
            
            // Internal endpoint - no JWT validation required
            // This endpoint is called by rumours service which uses the old path
            CurrencyUpdateResponseDto response = userManagementService.updateCurrency(id, updateDto, null);
            
            logger.info("Currency update successful for user ID: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (HttpClientErrorException ex) {
            logger.error("Client error updating currency for user {}: {}", id, ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            logger.error("Server error updating currency for user {}: {}", id, ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error updating currency for user {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                    "INTERNAL_ERROR", 
                    "An unexpected error occurred while updating currency"
                )));
        }
    }
}

package md.faf223.mafiaplatformgatewayservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.game.*;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.ErrorResponseDto;
import md.faf223.mafiaplatformgatewayservice.exceptions.MicroserviceException;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import md.faf223.mafiaplatformgatewayservice.services.grpc_communication.GameServiceGrpcCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameServiceGrpcCommunication gameService;
    private final JwtService jwtService;

    // ================== LOBBY ENDPOINTS ==================

    @PostMapping("/lobby")
    public ResponseEntity<?> createLobby(
            @Valid @RequestBody LobbyCreateDto createDto,
            HttpServletRequest request
    ) {
        try {
            // Extract and validate token
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

            // Check if the hostId matches the authenticated user
            if (!userIdFromToken.equals(createDto.getHostId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "FORBIDDEN", 
                        "Host ID must match authenticated user"
                    )));
            }

            // Create lobby via gRPC
            LobbyCreateResponseDto response = gameService.createLobby(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (MicroserviceException ex) {
            logger.error("Microservice error: {}", ex.getMessage());
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

    @PostMapping("/lobby/{lobbyId}/join")
    public ResponseEntity<?> joinLobby(
            @PathVariable Long lobbyId,
            HttpServletRequest request
    ) {
        try {
            // Extract and validate token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            String token = authHeader.substring(7);
            
            // Validate token and extract user info
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            
            if (username == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            // Join lobby via gRPC
            LobbyJoinResponseDto response = gameService.joinLobby(lobbyId, userId, username);
            return ResponseEntity.ok(response);
            
        } catch (MicroserviceException ex) {
            logger.error("Microservice error: {}", ex.getMessage());
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

    @PostMapping("/lobby/{lobbyId}/start")
    public ResponseEntity<?> startGame(
            @PathVariable Long lobbyId,
            HttpServletRequest request
    ) {
        try {
            // Extract and validate token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            String token = authHeader.substring(7);
            
            // Validate token and extract host ID
            String username = jwtService.extractUsername(token);
            Long hostId = jwtService.extractUserId(token);
            
            logger.info("Starting game - lobbyId: {}, username: {}, hostId from token: {}", lobbyId, username, hostId);
            
            if (username == null || hostId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            // Start game via gRPC
            GameStartResponseDto response = gameService.startGame(lobbyId, hostId);
            return ResponseEntity.ok(response);
            
        } catch (MicroserviceException ex) {
            logger.error("Microservice error: {}", ex.getMessage());
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

    // ================== GAME ENDPOINTS ==================

    @GetMapping("/{gameId}/state")
    public ResponseEntity<?> getGameState(@PathVariable Long gameId) {
        try {
            // Get game state via gRPC
            GameStateResponseDto response = gameService.getGameState(gameId);
            return ResponseEntity.ok(response);
            
        } catch (MicroserviceException ex) {
            logger.error("Microservice error: {}", ex.getMessage());
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

    @GetMapping("/{gameId}/players/status")
    public ResponseEntity<?> getPlayersStatus(@PathVariable Long gameId) {
        try {
            // Get players status via gRPC
            PlayersStatusResponseDto response = gameService.getPlayersStatus(gameId);
            return ResponseEntity.ok(response);
            
        } catch (MicroserviceException ex) {
            logger.error("Microservice error: {}", ex.getMessage());
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

package md.faf223.mafiaplatformgatewayservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.game.*;
import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.ErrorResponseDto;
import md.faf223.mafiaplatformgatewayservice.services.GameServiceClient;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameServiceClient gameService;
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

            // Create lobby via service client
            LobbyCreateResponseDto response = gameService.createLobby(createDto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
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
            
            // Validate token
            String username = jwtService.extractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            // Join lobby via service client
            LobbyJoinResponseDto response = gameService.joinLobby(lobbyId, token);
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
            
            // Validate token
            String username = jwtService.extractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            // Start game via service client
            GameStartResponseDto response = gameService.startGame(lobbyId, token);
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

    // ================== GAME ENDPOINTS ==================

    @GetMapping("/{gameId}/state")
    public ResponseEntity<?> getGameState(
            @PathVariable Long gameId,
            HttpServletRequest request
    ) {
        try {
            // Internal endpoint - no JWT required
            // Called by other services without authentication
            
            // Get game state via service client (no token needed)
            GameStateResponseDto response = gameService.getGameState(gameId, null);
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

    @GetMapping("/{gameId}/players/status")
    public ResponseEntity<?> getPlayersStatus(
            @PathVariable Long gameId,
            HttpServletRequest request
    ) {
        try {
            // Internal endpoint - no JWT required
            // Called by other services without authentication
            
            // Get players status via service client (no token needed)
            PlayersStatusResponseDto response = gameService.getPlayersStatus(gameId, null);
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

    @PutMapping("/{gameId}/players/{playerId}/status")
    public ResponseEntity<?> updatePlayerStatus(
            @PathVariable Long gameId,
            @PathVariable Long playerId,
            @Valid @RequestBody PlayerStatusUpdateDto updateDto,
            HttpServletRequest request
    ) {
        try {
            // Internal endpoint - no JWT required
            // Called by other services without authentication
            
            // Update player status via service client (no token needed)
            PlayerStatusUpdateResponseDto response = gameService.updatePlayerStatus(gameId, playerId, updateDto, null);
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

    @GetMapping("/{gameId}/events")
    public ResponseEntity<?> getGameEvents(
            @PathVariable Long gameId,
            HttpServletRequest request
    ) {
        try {
            // Internal endpoint - no JWT required
            // Called by other services without authentication
            
            // Get game events via service client (no token needed)
            GameEventsResponseDto response = gameService.getGameEvents(gameId, null);
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

    @GetMapping("/{gameId}/players-roles")
    public ResponseEntity<?> getPlayersRoles(
            @PathVariable Long gameId,
            HttpServletRequest request
    ) {
        try {
            // Internal endpoint - no JWT required
            // Called by other services without authentication
            
            // Get players roles via service client (no token needed)
            PlayersRolesResponseDto response = gameService.getPlayersRoles(gameId, null);
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

    @PostMapping("/{gameId}/voting")
    public ResponseEntity<?> submitVote(
            @PathVariable Long gameId,
            @Valid @RequestBody VoteDto voteDto,
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
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(new ErrorResponseDto.ErrorDetail(
                        "INVALID_TOKEN", 
                        "Invalid or expired token"
                    )));
            }

            // Submit vote via service client
            VoteResponseDto response = gameService.submitVote(gameId, voteDto, token);
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

    @PostMapping("/{gameId}/voting/elimination")
    public ResponseEntity<?> processElimination(
            @PathVariable Long gameId,
            @Valid @RequestBody EliminationDto eliminationDto,
            HttpServletRequest request
    ) {
        try {
            // Internal endpoint - no JWT required
            // Called by other services without authentication
            
            // Process elimination via service client (no token needed)
            EliminationResponseDto response = gameService.processElimination(gameId, eliminationDto, null);
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

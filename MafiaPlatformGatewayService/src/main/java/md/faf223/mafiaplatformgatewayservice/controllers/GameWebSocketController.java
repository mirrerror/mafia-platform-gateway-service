package md.faf223.mafiaplatformgatewayservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.services.GameServiceWebSocketClient;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game/ws")
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketController {

    private final GameServiceWebSocketClient gameServiceWebSocketClient;
    private final JwtService jwtService;

    /**
     * Initialize WebSocket connection to lobby events
     * Client should call this endpoint before subscribing to STOMP topic
     * Then subscribe to: /api/topic/lobby/{lobbyId}/events
     */
    @PostMapping("/lobby/{lobbyId}/connect")
    public ResponseEntity<?> connectToLobbyEvents(
            @PathVariable Long lobbyId,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.replace("Bearer ", "");
            
            // Validate token and extract user info
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            
            if (username == null || userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", Map.of(
                        "code", "INVALID_TOKEN",
                        "message", "JWT token is invalid or expired"
                    )
                ));
            }

            // Connect to Game Service lobby WebSocket
            gameServiceWebSocketClient.connectToLobbyEvents(lobbyId, userId, username);
            
            log.info("User {} (ID: {}) connected to lobby {} events", username, userId, lobbyId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Connected to lobby events",
                "lobbyId", lobbyId,
                "topic", "/api/topic/lobby/" + lobbyId + "/events"
            ));
            
        } catch (Exception e) {
            log.error("Error connecting to lobby events", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", Map.of(
                    "code", "CONNECTION_FAILED",
                    "message", "Failed to connect to lobby events: " + e.getMessage()
                )
            ));
        }
    }

    /**
     * Initialize WebSocket connection to game events
     * Client should call this endpoint before subscribing to STOMP topic
     * Then subscribe to: /api/topic/game/{gameId}/events
     */
    @PostMapping("/game/{gameId}/connect")
    public ResponseEntity<?> connectToGameEvents(
            @PathVariable Long gameId,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.replace("Bearer ", "");
            
            // Validate token and extract user info
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            
            if (username == null || userId == null) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", Map.of(
                        "code", "INVALID_TOKEN",
                        "message", "JWT token is invalid or expired"
                    )
                ));
            }

            // Connect to Game Service game WebSocket
            gameServiceWebSocketClient.connectToGameEvents(gameId, userId, username);
            
            log.info("User {} (ID: {}) connected to game {} events", username, userId, gameId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Connected to game events",
                "gameId", gameId,
                "topic", "/api/topic/game/" + gameId + "/events"
            ));
            
        } catch (Exception e) {
            log.error("Error connecting to game events", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", Map.of(
                    "code", "CONNECTION_FAILED",
                    "message", "Failed to connect to game events: " + e.getMessage()
                )
            ));
        }
    }

    /**
     * Disconnect from lobby events
     */
    @PostMapping("/lobby/{lobbyId}/disconnect")
    public ResponseEntity<?> disconnectFromLobbyEvents(@PathVariable Long lobbyId) {
        try {
            gameServiceWebSocketClient.disconnectFromLobbyEvents(lobbyId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Disconnected from lobby events",
                "lobbyId", lobbyId
            ));
            
        } catch (Exception e) {
            log.error("Error disconnecting from lobby events", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", Map.of(
                    "code", "DISCONNECTION_FAILED",
                    "message", "Failed to disconnect from lobby events: " + e.getMessage()
                )
            ));
        }
    }

    /**
     * Disconnect from game events
     */
    @PostMapping("/game/{gameId}/disconnect")
    public ResponseEntity<?> disconnectFromGameEvents(@PathVariable Long gameId) {
        try {
            gameServiceWebSocketClient.disconnectFromGameEvents(gameId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Disconnected from game events",
                "gameId", gameId
            ));
            
        } catch (Exception e) {
            log.error("Error disconnecting from game events", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", Map.of(
                    "code", "DISCONNECTION_FAILED",
                    "message", "Failed to disconnect from game events: " + e.getMessage()
                )
            ));
        }
    }
}

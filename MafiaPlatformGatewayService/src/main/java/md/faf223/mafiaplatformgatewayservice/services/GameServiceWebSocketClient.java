package md.faf223.mafiaplatformgatewayservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class GameServiceWebSocketClient {

    private final WebSocketSenderService webSocketSenderService;
    private final ObjectMapper objectMapper;
    private final String gameServiceWsUrl;
    
    // Store active WebSocket sessions: key = lobbyId or gameId, value = WebSocketSession
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    
    public GameServiceWebSocketClient(
            WebSocketSenderService webSocketSenderService,
            ObjectMapper objectMapper,
            @Value("${services.game.host}") String host,
            @Value("${services.game.port}") String port) {
        this.webSocketSenderService = webSocketSenderService;
        this.objectMapper = objectMapper;
        this.gameServiceWsUrl = "ws://" + host + ":" + port;
    }

    /**
     * Connect to Game Service lobby WebSocket and forward events to Gateway clients
     * @param lobbyId The lobby ID
     * @param userId The user ID extracted from JWT
     * @param username The username extracted from JWT
     */
    public void connectToLobbyEvents(Long lobbyId, Long userId, String username) {
        String sessionKey = "lobby-" + lobbyId;
        
        // Check if already connected
        if (activeSessions.containsKey(sessionKey)) {
            log.info("Already connected to lobby {} events", lobbyId);
            return;
        }

        try {
            String wsUrl = gameServiceWsUrl + "/ws/lobby/" + lobbyId + "?user_id=" + userId + "&username=" + username;
            log.info("Connecting to Game Service lobby WebSocket: {}", wsUrl);

            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketHandler handler = new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("Connected to lobby {} events WebSocket", lobbyId);
                    activeSessions.put(sessionKey, session);
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                    try {
                        String payload = message.getPayload().toString();
                        log.debug("Received lobby event: {}", payload);
                        
                        // Parse the message to get event type
                        JsonNode jsonNode = objectMapper.readTree(payload);
                        
                        // Forward to Gateway clients subscribed to this lobby
                        String destination = "/api/topic/lobby/" + lobbyId + "/events";
                        webSocketSenderService.sendMessageToClients(destination, jsonNode);
                        
                    } catch (Exception e) {
                        log.error("Error handling lobby WebSocket message", e);
                    }
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) {
                    log.error("WebSocket transport error for lobby {}", lobbyId, exception);
                    activeSessions.remove(sessionKey);
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                    log.info("Disconnected from lobby {} events: {}", lobbyId, closeStatus);
                    activeSessions.remove(sessionKey);
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };

            client.execute(handler, null, URI.create(wsUrl));
            
        } catch (Exception e) {
            log.error("Failed to connect to lobby {} WebSocket", lobbyId, e);
            throw new RuntimeException("Failed to connect to lobby events", e);
        }
    }

    /**
     * Connect to Game Service game WebSocket and forward events to Gateway clients
     * @param gameId The game ID
     * @param userId The user ID extracted from JWT
     * @param username The username extracted from JWT
     */
    public void connectToGameEvents(Long gameId, Long userId, String username) {
        String sessionKey = "game-" + gameId;
        
        // Check if already connected
        if (activeSessions.containsKey(sessionKey)) {
            log.info("Already connected to game {} events", gameId);
            return;
        }

        try {
            String wsUrl = gameServiceWsUrl + "/ws/game/" + gameId + "?user_id=" + userId + "&username=" + username;
            log.info("Connecting to Game Service game WebSocket: {}", wsUrl);

            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketHandler handler = new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("Connected to game {} events WebSocket", gameId);
                    activeSessions.put(sessionKey, session);
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                    try {
                        String payload = message.getPayload().toString();
                        log.debug("Received game event: {}", payload);
                        
                        // Parse the message to get event type
                        JsonNode jsonNode = objectMapper.readTree(payload);
                        
                        // Forward to Gateway clients subscribed to this game
                        String destination = "/api/topic/game/" + gameId + "/events";
                        webSocketSenderService.sendMessageToClients(destination, jsonNode);
                        
                    } catch (Exception e) {
                        log.error("Error handling game WebSocket message", e);
                    }
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) {
                    log.error("WebSocket transport error for game {}", gameId, exception);
                    activeSessions.remove(sessionKey);
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                    log.info("Disconnected from game {} events: {}", gameId, closeStatus);
                    activeSessions.remove(sessionKey);
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };

            client.execute(handler, null, URI.create(wsUrl));
            
        } catch (Exception e) {
            log.error("Failed to connect to game {} WebSocket", gameId, e);
            throw new RuntimeException("Failed to connect to game events", e);
        }
    }

    /**
     * Disconnect from lobby events WebSocket
     * @param lobbyId The lobby ID
     */
    public void disconnectFromLobbyEvents(Long lobbyId) {
        String sessionKey = "lobby-" + lobbyId;
        WebSocketSession session = activeSessions.remove(sessionKey);
        
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("Disconnected from lobby {} events", lobbyId);
            } catch (Exception e) {
                log.error("Error closing lobby WebSocket session", e);
            }
        }
    }

    /**
     * Disconnect from game events WebSocket
     * @param gameId The game ID
     */
    public void disconnectFromGameEvents(Long gameId) {
        String sessionKey = "game-" + gameId;
        WebSocketSession session = activeSessions.remove(sessionKey);
        
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("Disconnected from game {} events", gameId);
            } catch (Exception e) {
                log.error("Error closing game WebSocket session", e);
            }
        }
    }

    /**
     * Close all active WebSocket connections on shutdown
     */
    @PreDestroy
    public void cleanup() {
        log.info("Closing all Game Service WebSocket connections");
        activeSessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (Exception e) {
                log.error("Error closing WebSocket session", e);
            }
        });
        activeSessions.clear();
    }
}

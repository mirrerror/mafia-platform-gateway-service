package md.faf223.mafiaplatformgatewayservice.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.services.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketProxyHandler extends AbstractWebSocketHandler {

    private final JwtService jwtService;
    
    @Value("${services.game.host}")
    private String gameServiceHost;
    
    @Value("${services.game.port}")
    private String gameServicePort;
    
    // Store client session -> game service session mapping
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession clientSession) throws Exception {
        log.info("Client WebSocket connection established: {}", clientSession.getId());
        
        try {
            // Extract token from query parameters
            String token = extractTokenFromSession(clientSession);
            
            if (token == null || token.isEmpty()) {
                log.error("No token provided in WebSocket connection");
                clientSession.close(new CloseStatus(4001, "No token provided"));
                return;
            }
            
            // Validate JWT and extract user info
            String username;
            Long userId;
            
            try {
                username = jwtService.extractUsername(token);
                userId = jwtService.extractUserId(token);
                
                if (username == null || userId == null) {
                    log.error("Invalid token: unable to extract user info");
                    clientSession.close(new CloseStatus(4001, "Invalid token"));
                    return;
                }
            } catch (Exception e) {
                log.error("Token validation failed", e);
                clientSession.close(new CloseStatus(4001, "Invalid or expired token"));
                return;
            }
            
            // Extract the original path (e.g., /ws/game/123 or /ws/lobby/456)
            String originalPath = extractPathFromSession(clientSession);
            
            if (originalPath == null) {
                log.error("Invalid WebSocket path");
                clientSession.close(new CloseStatus(4004, "Invalid path"));
                return;
            }
            
            // Build Game Service WebSocket URL with user_id and username
            String gameServiceUrl = UriComponentsBuilder
                    .fromUriString("ws://" + gameServiceHost + ":" + gameServicePort + originalPath)
                    .queryParam("user_id", userId)
                    .queryParam("username", username)
                    .build()
                    .toUriString();
            
            log.info("Proxying WebSocket connection to Game Service: {} (user: {}, id: {})", 
                    gameServiceUrl, username, userId);
            
            // Create WebSocket client to connect to Game Service
            StandardWebSocketClient client = new StandardWebSocketClient();
            
            // Connect to Game Service
            WebSocketSession gameServiceSession = client.execute(
                new AbstractWebSocketHandler() {
                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        // Forward messages from Game Service to client
                        log.info("Received message from Game Service: {}", message.getPayload());
                        if (clientSession.isOpen()) {
                            clientSession.sendMessage(message);
                            log.info("Forwarded message from Game Service to client: {}", message.getPayload());
                        } else {
                            log.warn("Client session closed, cannot forward message from Game Service");
                        }
                    }
                    
                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                        log.info("Game Service connection closed: {}", status);
                        if (clientSession.isOpen()) {
                            clientSession.close(status);
                        }
                        sessionMap.remove(clientSession.getId());
                    }
                    
                    @Override
                    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                        log.error("Transport error from Game Service", exception);
                        if (clientSession.isOpen()) {
                            clientSession.close(new CloseStatus(1011, "Game Service error"));
                        }
                        sessionMap.remove(clientSession.getId());
                    }
                },
                null,
                URI.create(gameServiceUrl)
            ).get();
            
            // Store the mapping
            sessionMap.put(clientSession.getId(), gameServiceSession);
            log.info("WebSocket proxy established successfully for user {} ({})", username, userId);
            
        } catch (Exception e) {
            log.error("Failed to establish WebSocket proxy", e);
            if (clientSession.isOpen()) {
                clientSession.close(new CloseStatus(1011, "Proxy error: " + e.getMessage()));
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession clientSession, WebSocketMessage<?> message) throws Exception {
        // Forward messages from client to Game Service
        log.info("Received message from client: {}", message.getPayload());
        WebSocketSession gameServiceSession = sessionMap.get(clientSession.getId());
        
        if (gameServiceSession != null && gameServiceSession.isOpen()) {
            gameServiceSession.sendMessage(message);
            log.info("Forwarded message from client to Game Service: {}", message.getPayload());
        } else {
            log.warn("No active Game Service session for client {}", clientSession.getId());
            clientSession.close(new CloseStatus(1011, "No backend connection"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession clientSession, CloseStatus status) throws Exception {
        log.info("Client WebSocket connection closed: {} - {}", clientSession.getId(), status);
        
        // Close Game Service connection
        WebSocketSession gameServiceSession = sessionMap.remove(clientSession.getId());
        if (gameServiceSession != null && gameServiceSession.isOpen()) {
            gameServiceSession.close(status);
            log.info("Closed Game Service connection for client {}", clientSession.getId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession clientSession, Throwable exception) throws Exception {
        log.error("Transport error from client", exception);
        
        // Close both connections
        WebSocketSession gameServiceSession = sessionMap.remove(clientSession.getId());
        if (gameServiceSession != null && gameServiceSession.isOpen()) {
            gameServiceSession.close(new CloseStatus(1011, "Client error"));
        }
        
        if (clientSession.isOpen()) {
            clientSession.close(new CloseStatus(1011, "Transport error"));
        }
    }

    /**
     * Extract JWT token from query parameters
     */
    private String extractTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring(6); // Remove "token="
                }
            }
        }
        return null;
    }

    /**
     * Extract the path from the WebSocket session (e.g., /ws/game/123)
     */
    private String extractPathFromSession(WebSocketSession session) {
        return session.getUri().getPath();
    }
}

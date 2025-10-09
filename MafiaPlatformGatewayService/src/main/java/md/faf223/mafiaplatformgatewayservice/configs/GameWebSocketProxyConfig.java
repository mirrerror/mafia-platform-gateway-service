package md.faf223.mafiaplatformgatewayservice.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class GameWebSocketProxyConfig implements WebSocketConfigurer {

    private final GameWebSocketProxyHandler gameWebSocketProxyHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register WebSocket proxy endpoints
        registry.addHandler(gameWebSocketProxyHandler, "/ws/game/**", "/ws/lobby/**")
                .setAllowedOrigins("*"); // Allow all origins for development
    }
}

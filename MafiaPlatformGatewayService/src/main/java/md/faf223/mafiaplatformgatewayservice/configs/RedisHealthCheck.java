package md.faf223.mafiaplatformgatewayservice.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthCheck {

    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void checkRedisConnection() {
        log.debug("========================================");
        log.debug("Starting Redis Connection Health Check");
        log.debug("========================================");

        try {
            RedisConnection connection = redisConnectionFactory.getConnection();
            String pong = connection.ping();
            log.debug("✅ Redis PING successful: {}", pong);
            connection.close();

            String testKey = "health:check:" + System.currentTimeMillis();
            String testValue = "Redis is working!";

            redisTemplate.opsForValue().set(testKey, testValue);
            log.debug("✅ Redis WRITE successful");

            Object retrieved = redisTemplate.opsForValue().get(testKey);
            log.debug("✅ Redis READ successful: {}", retrieved);

            redisTemplate.delete(testKey);
            log.debug("✅ Redis DELETE successful");

            log.debug("========================================");
            log.debug("✅ Redis is FULLY OPERATIONAL");
            log.debug("========================================");

        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ Redis Connection FAILED!");
            log.error("========================================");
            log.error("Error: {}", e.getMessage());
            log.error("Cause: {}", e.getCause() != null ? e.getCause().getMessage() : "Unknown");
            log.error("Connection Factory: {}", redisConnectionFactory);
            log.error("========================================");
            log.error("Troubleshooting Steps:");
            log.error("1. Check if Redis container is running: docker ps | grep redis");
            log.error("2. Check Redis logs: docker logs gateway-redis");
            log.error("3. Test Redis connectivity: docker exec gateway-redis redis-cli ping");
            log.error("4. Check network: docker exec gateway-service ping redis");
            log.error("5. Verify SPRING_REDIS_HOST environment variable");
            log.error("========================================");
        }
    }
}

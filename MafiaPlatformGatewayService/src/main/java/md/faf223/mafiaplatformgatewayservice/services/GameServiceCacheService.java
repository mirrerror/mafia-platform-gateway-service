package md.faf223.mafiaplatformgatewayservice.services;

import md.faf223.mafiaplatformgatewayservice.dtos.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Cache service for Game Service operations
 * Provides Redis caching for frequently accessed game data
 */
@Service
public class GameServiceCacheService {

    private static final Logger logger = LoggerFactory.getLogger(GameServiceCacheService.class);
    private static final String GAME_STATE_CACHE = "gameState";
    private static final String PLAYERS_STATUS_CACHE = "playersStatus";
    private static final String GAME_EVENTS_CACHE = "gameEvents";
    private static final String PLAYERS_ROLES_CACHE = "playersRoles";
    private static final String USER_AUTH_CACHE = "userAuth";
    
    private static final long GAME_STATE_TTL = 30; // 30 seconds for real-time game state
    private static final long PLAYERS_STATUS_TTL = 30; // 30 seconds for player status
    private static final long GAME_EVENTS_TTL = 60; // 1 minute for game events
    private static final long PLAYERS_ROLES_TTL = 300; // 5 minutes for roles (doesn't change often)
    private static final long USER_AUTH_TTL = 900; // 15 minutes for user auth data

    private final RedisTemplate<String, Object> redisTemplate;

    public GameServiceCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== GAME STATE CACHING ====================

    @Cacheable(value = GAME_STATE_CACHE, key = "#gameId", unless = "#result == null")
    public GameStateResponseDto getCachedGameState(Long gameId) {
        logger.debug("Cache miss for game state: {}", gameId);
        return null; // Will be populated by caller
    }

    public void cacheGameState(Long gameId, GameStateResponseDto state) {
        if (state != null) {
            redisTemplate.opsForValue().set(
                GAME_STATE_CACHE + "::" + gameId, 
                state, 
                GAME_STATE_TTL, 
                TimeUnit.SECONDS
            );
            logger.debug("Cached game state for game: {} with TTL: {}s", gameId, GAME_STATE_TTL);
        }
    }

    @CacheEvict(value = GAME_STATE_CACHE, key = "#gameId")
    public void evictGameState(Long gameId) {
        logger.debug("Evicted game state cache for game: {}", gameId);
    }

    // ==================== PLAYERS STATUS CACHING ====================

    @Cacheable(value = PLAYERS_STATUS_CACHE, key = "#gameId", unless = "#result == null")
    public PlayersStatusResponseDto getCachedPlayersStatus(Long gameId) {
        logger.debug("Cache miss for players status: {}", gameId);
        return null;
    }

    public void cachePlayersStatus(Long gameId, PlayersStatusResponseDto status) {
        if (status != null) {
            redisTemplate.opsForValue().set(
                PLAYERS_STATUS_CACHE + "::" + gameId,
                status,
                PLAYERS_STATUS_TTL,
                TimeUnit.SECONDS
            );
            logger.debug("Cached players status for game: {} with TTL: {}s", gameId, PLAYERS_STATUS_TTL);
        }
    }

    @CacheEvict(value = PLAYERS_STATUS_CACHE, key = "#gameId")
    public void evictPlayersStatus(Long gameId) {
        logger.debug("Evicted players status cache for game: {}", gameId);
    }

    // ==================== GAME EVENTS CACHING ====================

    @Cacheable(value = GAME_EVENTS_CACHE, key = "#gameId", unless = "#result == null")
    public GameEventsResponseDto getCachedGameEvents(Long gameId) {
        logger.debug("Cache miss for game events: {}", gameId);
        return null;
    }

    public void cacheGameEvents(Long gameId, GameEventsResponseDto events) {
        if (events != null) {
            redisTemplate.opsForValue().set(
                GAME_EVENTS_CACHE + "::" + gameId,
                events,
                GAME_EVENTS_TTL,
                TimeUnit.SECONDS
            );
            logger.debug("Cached game events for game: {} with TTL: {}s", gameId, GAME_EVENTS_TTL);
        }
    }

    @CacheEvict(value = GAME_EVENTS_CACHE, key = "#gameId")
    public void evictGameEvents(Long gameId) {
        logger.debug("Evicted game events cache for game: {}", gameId);
    }

    // ==================== PLAYERS ROLES CACHING ====================

    @Cacheable(value = PLAYERS_ROLES_CACHE, key = "#gameId", unless = "#result == null")
    public PlayersRolesResponseDto getCachedPlayersRoles(Long gameId) {
        logger.debug("Cache miss for players roles: {}", gameId);
        return null;
    }

    public void cachePlayersRoles(Long gameId, PlayersRolesResponseDto roles) {
        if (roles != null) {
            redisTemplate.opsForValue().set(
                PLAYERS_ROLES_CACHE + "::" + gameId,
                roles,
                PLAYERS_ROLES_TTL,
                TimeUnit.SECONDS
            );
            logger.debug("Cached players roles for game: {} with TTL: {}s", gameId, PLAYERS_ROLES_TTL);
        }
    }

    @CacheEvict(value = PLAYERS_ROLES_CACHE, key = "#gameId")
    public void evictPlayersRoles(Long gameId) {
        logger.debug("Evicted players roles cache for game: {}", gameId);
    }

    // ==================== USER AUTHENTICATION DATA CACHING ====================

    /**
     * Cache user authentication data (userId, username) extracted from JWT
     */
    public void cacheUserAuthData(String token, Long userId, String username) {
        if (token != null && userId != null && username != null) {
            UserAuthData authData = new UserAuthData(userId, username);
            redisTemplate.opsForValue().set(
                USER_AUTH_CACHE + "::" + token,
                authData,
                USER_AUTH_TTL,
                TimeUnit.SECONDS
            );
            logger.debug("Cached auth data for user: {} (ID: {}) with TTL: {}s", username, userId, USER_AUTH_TTL);
        }
    }

    /**
     * Retrieve cached user authentication data
     */
    public UserAuthData getCachedUserAuthData(String token) {
        if (token == null) return null;
        
        Object cached = redisTemplate.opsForValue().get(USER_AUTH_CACHE + "::" + token);
        if (cached instanceof UserAuthData) {
            logger.debug("Cache hit for auth data token");
            return (UserAuthData) cached;
        }
        logger.debug("Cache miss for auth data token");
        return null;
    }

    /**
     * Evict user authentication data (e.g., on logout)
     */
    public void evictUserAuthData(String token) {
        if (token != null) {
            redisTemplate.delete(USER_AUTH_CACHE + "::" + token);
            logger.debug("Evicted auth data for token");
        }
    }

    // ==================== CACHE INVALIDATION FOR GAME UPDATES ====================

    /**
     * Evict all cache entries for a specific game
     * Call this when game state changes (player actions, game phase changes, etc.)
     */
    public void evictAllGameCache(Long gameId) {
        evictGameState(gameId);
        evictPlayersStatus(gameId);
        evictGameEvents(gameId);
        evictPlayersRoles(gameId);
        logger.info("Evicted all cache entries for game: {}", gameId);
    }

    // ==================== HELPER CLASSES ====================

    /**
     * DTO for storing user authentication data in cache
     */
    public static class UserAuthData {
        private Long userId;
        private String username;

        public UserAuthData() {}

        public UserAuthData(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}

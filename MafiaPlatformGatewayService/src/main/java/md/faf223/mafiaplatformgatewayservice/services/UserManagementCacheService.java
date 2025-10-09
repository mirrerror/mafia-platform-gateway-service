package md.faf223.mafiaplatformgatewayservice.services;

import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.UserProfileResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Cache service for User Management Service operations
 * Provides Redis caching for user profile data
 */
@Service
public class UserManagementCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementCacheService.class);
    private static final String USER_PROFILE_CACHE = "userProfile";
    
    private static final long USER_PROFILE_TTL = 300; // 5 minutes for user profile data

    private final RedisTemplate<String, Object> redisTemplate;

    public UserManagementCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== USER PROFILE CACHING ====================

    /**
     * Get cached user profile
     */
    @Cacheable(value = USER_PROFILE_CACHE, key = "#userId", unless = "#result == null")
    public UserProfileResponseDto getCachedUserProfile(Long userId) {
        logger.debug("Cache miss for user profile: {}", userId);
        return null; // Will be populated by caller
    }

    /**
     * Cache user profile data
     */
    public void cacheUserProfile(Long userId, UserProfileResponseDto profile) {
        if (profile != null && userId != null) {
            redisTemplate.opsForValue().set(
                USER_PROFILE_CACHE + "::" + userId, 
                profile, 
                USER_PROFILE_TTL, 
                TimeUnit.SECONDS
            );
            logger.debug("Cached user profile for user: {} with TTL: {}s", userId, USER_PROFILE_TTL);
        }
    }

    /**
     * Evict user profile cache
     * Call this when user profile is updated
     */
    @CacheEvict(value = USER_PROFILE_CACHE, key = "#userId")
    public void evictUserProfile(Long userId) {
        logger.debug("Evicted user profile cache for user: {}", userId);
    }

    /**
     * Evict user profile cache when currency is updated
     */
    public void evictUserProfileOnCurrencyUpdate(Long userId) {
        evictUserProfile(userId);
        logger.info("Evicted user profile cache due to currency update for user: {}", userId);
    }
}

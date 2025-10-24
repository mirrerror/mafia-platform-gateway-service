package md.faf223.mafiaplatformgatewayservice.services;

import md.faf223.mafiaplatformgatewayservice.dtos.usermanagement.UserProfileResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Cache service for User Management Service operations
 * Provides Redis caching for user profile data with 10-second TTL
 */
@Service
public class UserManagementCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementCacheService.class);
    private static final String USER_PROFILE_CACHE = "userProfile";

    // ==================== USER PROFILE CACHING ====================

    /**
     * Get cached user profile
     * Returns null if not in cache (cache miss)
     */
    @Cacheable(value = USER_PROFILE_CACHE, key = "#userId", unless = "#result == null")
    public UserProfileResponseDto getCachedUserProfile(Long userId) {
        logger.debug("Cache miss for user profile: {}", userId);
        return null; // Will be populated by caller
    }

    /**
     * Cache user profile data
     * Uses @CachePut to update the cache with the new value
     */
    @CachePut(value = USER_PROFILE_CACHE, key = "#userId", unless = "#profile == null")
    public UserProfileResponseDto cacheUserProfile(Long userId, UserProfileResponseDto profile) {
        if (profile != null && userId != null) {
            logger.debug("Cached user profile for user: {} (TTL: 10s from CacheManager)", userId);
        }
        return profile;
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

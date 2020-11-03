package com.github.frcsty.discordminecrafthook.cache;

import com.github.frcsty.discordminecrafthook.cache.listener.RequestRemovalListener;
import com.github.frcsty.discordminecrafthook.config.ConfigStorage;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles our Code Request Cache,
 * invalidates codes after 5minutes of creation
 */
public final class RequestCache {

    @NotNull private final Cache<String, UUID> requestCache;

    /**
     * Builds our Timed Cache with predefined parameters
     */
    public RequestCache(final ConfigStorage configStorage) {
        this.requestCache = CacheBuilder.newBuilder()
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .maximumSize(500)
                //.removalListener(new RequestRemovalListener(configStorage))
                .build();
    }

    /**
     * Adds a cache for a specific player to the cache
     *
     * @param code              The player specific code
     * @param playerIdentifier  Specified player's {@link UUID}
     */
    public void addCodeToCache(final String code, final UUID playerIdentifier) {
        this.requestCache.put(code, playerIdentifier);
    }

    /**
     * Returns the {@link UUID} associated to the given code,
     * or null if there is none
     *
     * @param code  Specified code in interest
     * @return  Returns a {@link UUID} associated to specified code or null
     */
    @Nullable public UUID getUUIDAssociatedTo(final String code) {
        return this.requestCache.getIfPresent(code);
    }

    /**
     * Invalidates desired code if it exists within the Cache
     *
     * @param code  Code specified to be invalidated
     */
    public void invalidateCode(final String code) {
        this.requestCache.invalidate(code);
    }

    /**
     * Invalidates all residual codes for a specified user
     * Ensures cleanup of unused codes to prevent any mess-ups
     *
     * @param playerIdentifier Specified player's {@link UUID}
     */
    public void invalidateUserCodes(final UUID playerIdentifier) {
        final ConcurrentMap<String, UUID> cacheAsMap = this.requestCache.asMap();

        for (final String code : cacheAsMap.keySet()) {
            final UUID identifier = cacheAsMap.get(code);

            if (identifier == null) continue;
            if (identifier.equals(playerIdentifier)) {
                this.requestCache.invalidate(code);
            }
        }
    }

}

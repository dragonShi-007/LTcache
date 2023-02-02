package com.dragon.LTcache.core.cache;

import com.dragon.LTcache.core.config.LTCacheProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Descreption TODO
 * @Author dragon-Shi
 * @Date 2023/2/1 14:17
 * @Version 1.0
 **/
public class LTCacheManager implements CacheManager {

    private Map<String,Cache> caches = new ConcurrentHashMap<>();
    private LTCacheProperties properties;
    private RedisCache redisCache;

    public LTCacheManager(RedisCache cache,LTCacheProperties properties){
        super();
        this.redisCache = cache;
        this.properties = properties;
    }

    @Override
    public Cache getCache(String name) {
        return this.caches.computeIfAbsent(name, (cacheName) -> {
            return new LTCache(name, caffeineCache(), redisCache, properties);
        });
    }

    public com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache(){
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
        if(properties.getCaffeine().getExpireAfterAccess() > 0) {
            cacheBuilder.expireAfterAccess(properties.getCaffeine().getExpireAfterAccess(), TimeUnit.SECONDS);
        }
        if(properties.getCaffeine().getExpireAfterWrite() > 0) {
            cacheBuilder.expireAfterWrite(properties.getCaffeine().getExpireAfterWrite(), TimeUnit.SECONDS);
        }
        if(properties.getCaffeine().getInitialCapacity() > 0) {
            cacheBuilder.initialCapacity(properties.getCaffeine().getInitialCapacity());
        }
        if(properties.getCaffeine().getMaximumSize() > 0) {
            cacheBuilder.maximumSize(properties.getCaffeine().getMaximumSize());
        }
        if(properties.getCaffeine().getRefreshAfterWrite() > 0) {
            cacheBuilder.refreshAfterWrite(properties.getCaffeine().getRefreshAfterWrite(), TimeUnit.SECONDS);
        }
        return cacheBuilder.build();
    }

    @Override
    public Collection<String> getCacheNames() {
        return null;
    }

}

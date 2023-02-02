package com.dragon.LTcache.core.cache;

import com.dragon.LTcache.core.config.LTCacheProperties;
import com.dragon.LTcache.core.model.CacheData;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Descreption TODO
 * @Author shizhongxu3
 * @Date 2023/2/1 14:20
 * @Version 1.0
 **/
public class LTCache extends AbstractValueAdaptingCache {

    private String cacheName;

    private Cache<Object, Object> caffeineCache;

    private RedisCache redisCache;

    private LTCacheProperties properties;

    //过期时间
    private Map<String, Long> expires;

    private Map<String, ReentrantLock> lockMap;

    private boolean ifL1Open = false;

    protected LTCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    public LTCache(String cacheName,
                   Cache<Object, Object> caffeineCache,
                   RedisCache redisCache,
                   LTCacheProperties properties
    ) {
        super(properties.isAllowNullValues());
        this.cacheName = cacheName;
        this.caffeineCache = caffeineCache;
        this.redisCache = redisCache;
        this.properties = properties;
        this.expires = properties.getRedis().getExpires();
        this.lockMap = new HashMap<>();
        this.ifL1Open = properties.getL1CacheNameSet()!=null && properties.getL1CacheNameSet().contains(cacheName);
    }

    @Override
    public String getName() {
        return this.cacheName;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    protected Object lookup(Object key) {
        Object value = null;
        String cacheKey = getKey(key);
        if (ifL1Open) {
            value = caffeineCache.getIfPresent(key);
            if (value != null) {
                return value;
            }
        }
        value = redisCache.get(cacheKey);
        if (value != null && ifL1Open) {
            caffeineCache.put(key, toStoreValue(value));
        }
        return value;
    }





    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        {
            Object value = lookup(key);
            if (value != null) {
                return (T) value;
            }

            ReentrantLock lock = lockMap.get(key.toString());
            if (lock == null) {
                lock = new ReentrantLock();
                lockMap.putIfAbsent(key.toString(), lock);
            }
            try {
                lock.lock();
                value = lookup(key);
                if (value != null) {
                    return (T) value;
                }
                //代表走被拦截的方法逻辑,并返回方法的返回结果
                value = valueLoader.call();
                Object storeValue = toStoreValue(value);
                put(key, storeValue);
                return (T) value;
            } catch (Exception e) {
                throw new ValueRetrievalException(key, valueLoader, e.getCause());
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void put(Object key, Object value) {
        if (!super.isAllowNullValues() && value == null) {
            this.evict(key);
            return;
        }
        long expire = getExpire();
        if (expire > 0) {
            redisCache.set(getKey(key), toStoreValue(value), expire);
        } else {
            redisCache.set(getKey(key), toStoreValue(value));
        }

        if (ifL1Open) {
            //通知其它节点
            push(new CacheData(this.cacheName, key));
            caffeineCache.put(key, toStoreValue(value));
        }
    }

    private String getKey(Object key) {
        return this.cacheName.concat(":").concat(key.toString());
    }

    private void push(CacheData cache){
        redisCache.getRedisTemplate().convertAndSend(properties.getRedis().getTopic(), cache);
    }

    private long getExpire() {
        // 过期时间不允许为空
        return expires.get(this.cacheName);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public void evict(Object key) {
        redisCache.delete(getKey(key));

        push(new CacheData(this.cacheName, key));

        caffeineCache.invalidate(key);
    }

    @Override
    public void clear() {
        // 先清除redis中缓存数据，然后清除caffeine中的缓存，避免短时间内如果先清除caffeine缓存后其他请求会再从redis里加载到caffeine中
        Set<String> keys = redisCache.keys(this.cacheName.concat(":*"));
        for (String key : keys) {
            redisCache.delete(key);
        }
        push(new CacheData(this.cacheName, null));
        caffeineCache.invalidateAll();
    }

    @Override
    public boolean invalidate() {
        return false;
    }
}

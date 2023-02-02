package com.dragon.LTcache.core.listener;

import com.dragon.LTcache.core.cache.LTCacheManager;
import com.dragon.LTcache.core.cache.RedisCache;
import com.dragon.LTcache.core.model.CacheData;
import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @Descreption TODO
 * @Author shizhongxu3
 * @Date 2023/2/1 17:16
 * @Version 1.0
 **/
public class CacheDataListener implements MessageListener {

    private RedisCache redisCache;
    private LTCacheManager redisCaffeineCacheManager;

    public CacheDataListener(RedisCache redisCache, LTCacheManager redisCaffeineCacheManager) {
        this.redisCache = redisCache;
        this.redisCaffeineCacheManager = redisCaffeineCacheManager;
    }

    // 接受其他节点发送的一级缓存失效通知
    // 一级缓存变更(删，改)，各节点会相互通知删除
    @Override
    public void onMessage(Message message, byte[] pattern) {
        CacheData cacheData = (CacheData)this.redisCache.getRedisTemplate().getKeySerializer().deserialize(pattern);
        Cache cache = redisCaffeineCacheManager.getCache(cacheData.getCacheName());
        if(cache != null){
            cache.clear();
        }
    }
}

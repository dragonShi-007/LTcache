package com.dragon.LTcache.starter;

import com.dragon.LTcache.core.cache.LTCacheManager;
import com.dragon.LTcache.core.cache.RedisCache;
import com.dragon.LTcache.core.config.LTCacheProperties;
import com.dragon.LTcache.core.listener.CacheDataListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;

/**
 * @Descreption TODO
 * @Author shizhongxu3
 * @Date 2023/1/28 14:50
 * @Version 1.0
 **/
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(LTCacheProperties.class)
public class LTcacheAutoConfiguration {

    @Autowired
    private LTCacheProperties properties;

    @Bean
    @ConditionalOnMissingBean(name = "customizeRedisTemplate")
    @Order(0)
    public RedisTemplate<Object, Object> stringKeyRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        return template;
    }

    @Bean
    @ConditionalOnBean(name = "customizeRedisTemplate")
    @Order(1)
    public RedisCache redisCache(RedisTemplate<Object, Object> customizeRedisTemplate) {
        RedisCache redisCache = new RedisCache();
        redisCache.setRedisTemplate(customizeRedisTemplate);
        return redisCache;
    }

    @Bean
    @ConditionalOnBean(RedisCache.class)
    @Order(2)
    public LTCacheManager cacheManager(RedisCache redisCache) {
        return new LTCacheManager(redisCache,properties);
    }

    @Bean
    @ConditionalOnClass(RedisCache.class)
    @Order(3)
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisCache redisCache,
                                                                       LTCacheManager cacheManager) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisCache.getRedisTemplate().getConnectionFactory());
        CacheDataListener cacheMessageListener = new CacheDataListener(redisCache, cacheManager);
        redisMessageListenerContainer.addMessageListener(cacheMessageListener, new ChannelTopic(properties.getRedis().getTopic()));
        return redisMessageListenerContainer;
    }

}

package com.dragon.LTcache.starter;

import com.dragon.LTcache.core.cache.RedisCache;
import com.dragon.LTcache.core.config.LTCacheProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

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

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    @Order(1)
    public RedisCache redisCache(RedisTemplate<Object, Object> redisTemplate) {
        RedisCache redisCache = new RedisCache();
        redisCache.setRedisTemplate(redisTemplate);
        return redisCache;
    }




}

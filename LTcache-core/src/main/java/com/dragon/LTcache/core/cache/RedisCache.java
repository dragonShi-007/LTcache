package com.dragon.LTcache.core.cache;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Descreption TODO
 * @Author shizhongxu3
 * @Date 2023/1/28 15:46
 * @Version 1.0
 **/
public class RedisCache {
    // StringRedisTemplate继承RedisTemplate
    //两者的数据是不共通的；
    //
    //SDR默认采用的序列化策略有两种，一种是String的序列化策略，一种是JDK的序列化策略。
    //
    //StringRedisTemplate默认采用的是String的序列化策略，保存的key和value都是采用此策略序列化保存的。
    //
    //RedisTemplate默认采用的是JDK的序列化策略，保存的key和value都是采用此策略序列化保存的。
    private RedisTemplate<Object,Object> redisTemplate;

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

   // todo 封装redisTemplate操作
}

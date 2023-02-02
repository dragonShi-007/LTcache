package com.dragon.LTcache.core.cache;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Descreption TODO
 * @Author dragon-Shi
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
    //private ValueOperations<Object, Object> value = redisTemplate.opsForValue();
    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void set(String key,Object value,long timeout){
        redisTemplate.opsForValue().set(key, value, timeout,TimeUnit.MILLISECONDS);
    }

    public void set(String key,Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void hashPutAll(String key, Map<String, JSONObject> m){
        redisTemplate.opsForHash().putAll(key, m);
    }

    public void hashPut(String key,String filed,Object value){
        redisTemplate.opsForHash().put(key, filed, value);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hashEntries(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }


    public void hashDelete(String key,String filed){
        redisTemplate.opsForHash().delete(key, filed);
    }

    public Long setAdd(String key,Object value){
        Long result = redisTemplate.opsForSet().add(key, value);
        return result;
    }


    public Set setMembers(String key){
        Set resultSet = redisTemplate.opsForSet().members(key);
        return resultSet;
    }

    public boolean setIsMember(String key,Object value){
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Set setIntersect(String key1,String key2){
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    public Set setUnion(String key1,String key2){
        return redisTemplate.opsForSet().union(key1, key2);
    }

    public void listLeftPush(String key,String value){
        redisTemplate.opsForList().leftPush(key, value);
    }

    public void listLeftPush(String key,Object object){
        redisTemplate.opsForList().leftPush(key, object);
    }

    public Object listRightPop(String key){
        return redisTemplate.opsForList().rightPop(key);
    }

    public long increment(String key,long value){
        return redisTemplate.opsForValue().increment(key, value);
    }

    public long hashIncrement(String key,String hashKey,long value){
        return redisTemplate.opsForHash().increment(key, hashKey, value);
    }

    public void hashPut(String key,String filed,Long value){
        redisTemplate.opsForHash().put(key, filed, value.toString());
    }

    public Object hashGet(String key,String filed){
        return  redisTemplate.opsForHash().get(key, filed);
    }

    public long increment1(String key,long expire){
        return redisTemplate.opsForValue().increment(key,expire);
    }


    public Set<String> keys(String key){
        Set keys = redisTemplate.keys(key + "*");
        return new HashSet<>(keys);
    }


    public List multiGet(List keyList){
        return redisTemplate.opsForValue().multiGet(keyList);
    }
}

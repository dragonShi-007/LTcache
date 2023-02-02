package com.dragon.LTcache.core.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Descreption TODO
 * @Author dragon-Shi
 * @Date 2023/1/28 16:02
 * @Version 1.0
 **/
@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = "ltcache")
public class LTCacheProperties {

    private boolean allowNullValues = true;
    private Set<String> l1CacheNameSet = new HashSet<>();
    private final Caffeine caffeine = new Caffeine();
    private final Redis redis = new Redis();

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Caffeine{
        /**
         * 是否自动刷新过期缓存 true 表示是(默认)，false 表示否
         */
        private boolean autoRefreshExpireCache = false;

        /**
         * 缓存刷新调度线程池的大小
         * 默认为 CPU数 * 2
         */
        private Integer refreshPoolSize = Runtime.getRuntime().availableProcessors();

        /**
         * 缓存刷新的频率(秒)
         */
        private Long refreshPeriod = 30L;

        /**
         * 同一个key的发布消息频率(毫秒)
         */
        private Long publishMsgPeriodMilliSeconds = 500L;


        /** 访问后过期时间，单位秒*/
        private long expireAfterAccess;

        /** 写入后过期时间，单位秒*/
        private long expireAfterWrite;

        /** 写入后刷新时间，单位秒*/
        private long refreshAfterWrite;

        /** 初始化大小*/
        private int initialCapacity;

        /** 最大缓存对象个数，超过此数量时之前放入的缓存将失效*/
        private long maximumSize;
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Redis{

        /** 全局过期时间，单位毫秒，默认不过期*/
        private long defaultExpiration = 0;

        /** 每个cacheName的过期时间，单位毫秒，优先级比defaultExpiration高*/
        private Map<String, Long> expires = new HashMap<>();

        /** 缓存更新时通知其他节点的topic名称*/
        private String topic = "cache:redis:caffeine:topic";

    }
}

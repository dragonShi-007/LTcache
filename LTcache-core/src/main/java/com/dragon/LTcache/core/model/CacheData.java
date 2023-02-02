package com.dragon.LTcache.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Descreption TODO
 * @Author shizhongxu3
 * @Date 2023/2/1 14:54
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheData implements Serializable {

    private static final long serialVersionUID = 6885552604722517599L;

    private String cacheName;

    private Object key;
}
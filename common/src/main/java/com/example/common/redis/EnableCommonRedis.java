package com.example.common.redis;

import com.example.common.redis.config.CommonRedisConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 显式启用 common Redis 基础能力。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CommonRedisConfiguration.class)
public @interface EnableCommonRedis {
}

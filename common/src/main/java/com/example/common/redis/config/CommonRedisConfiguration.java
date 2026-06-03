package com.example.common.redis.config;

import com.example.common.redis.DefaultRedisService;
import com.example.common.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * common Redis 基础能力装配。
 *
 * <p>只有接入项目显式使用 @EnableCommonRedis 时才会导入本配置。</p>
 */
@Configuration(proxyBeanMethods = false)
public class CommonRedisConfiguration {

    @Bean
    public RedisService redisService(StringRedisTemplate stringRedisTemplate,
                                     ObjectProvider<ObjectMapper> objectMapperProvider) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(
                () -> new ObjectMapper().findAndRegisterModules()
        );
        return new DefaultRedisService(stringRedisTemplate, objectMapper);
    }
}

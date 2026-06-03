package com.example.study;

import com.example.common.web.EnableCommonWeb;
import com.example.common.web.EnableCommonAuthWeb;
import com.example.common.redis.EnableCommonRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenyile
 */
// app 主动接入 common-web；只引入 common 依赖本身不会自动启用 Web 能力。
@EnableCommonWeb
// 认证过滤器属于会拦截请求的能力，需要 app 明确选择启用。
@EnableCommonAuthWeb
// Redis 能力由 app 显式启用；只引入 common 不会强制接入 Redis。
@EnableCommonRedis
@SpringBootApplication
public class StudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyApplication.class, args);
    }

}

package com.example.study.demo.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 示例配置，属于 app demo 的具体认证方案，不放进 common。
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "demo.auth.jwt")
public class DemoJwtProperties {

    /**
     * HMAC 签名密钥；真实项目应从环境变量、配置中心或密钥管理系统读取。
     */
    private String secret = "study-demo-jwt-secret-change-me-at-least-32-bytes";

    /**
     * JWT 有效期，单位秒。
     */
    private long ttlSeconds = 7200;

}

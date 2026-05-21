package com.example.study.config.security;

import com.example.common.web.auth.CommonAuthProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 最小接入配置。
 *
 * <p>这个类属于 Spring Security 学习阶段一：先让请求进入 Spring Security 过滤器链，
 * 并显式写出前后端分离 API 常见的基础安全规则。这里暂时不读取 Bearer token，
 * 也不把现有 AuthContext 映射到 SecurityContextHolder，这些留到后续阶段对照学习。</p>
 */
// 本配置类没有在 @Bean 方法之间互相调用，关闭代理即可。
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    /**
     * 这里复用之前 common-web 里定义的认证配置。
     *
     * <p>它对应 application.yml 里的 common.auth.*，
     * 阶段一先借用其中的 permit-paths 作为 Spring Security 的匿名访问路径。</p>
     */
    @Resource
    private CommonAuthProperties commonAuthProperties;

    /**
     * 定义 app 模块自己的 Spring Security 过滤器链。
     *
     * <p>阶段一只做最小接入：登录、注册等白名单接口允许匿名访问，
     * 其他接口默认要求已认证。真正的 Bearer token 认证逻辑会在阶段二接入。</p>
     */
    @Bean
    public SecurityFilterChain appSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {
        // 复用 common.auth.permit-paths，避免 Spring Security 和自研 AuthFilter 的匿名路径不一致。
        String[] permitPaths = commonAuthProperties.getPermitPaths().toArray(String[]::new);

        http.csrf(AbstractHttpConfigurer::disable)
                // 当前项目是 JSON API Demo，不使用 Spring Security 默认登录页。
                .formLogin(AbstractHttpConfigurer::disable)
                // 不启用浏览器弹窗式 Basic 认证，后续统一走 Bearer token。
                .httpBasic(AbstractHttpConfigurer::disable)
                // 现有 logout 是业务接口，暂不使用 Spring Security 内置 logout 机制。
                .logout(AbstractHttpConfigurer::disable)
                // API 请求不需要保存原始访问地址用于登录后跳转。
                .requestCache(AbstractHttpConfigurer::disable)
                // 前后端分离 token 模式不依赖服务端 Session 保存登录态。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    // 登录、JWT 登录、注册等公开接口来自 application.yml 的 common.auth.permit-paths。
                    if (permitPaths.length > 0) {
                        authorize.requestMatchers(permitPaths).permitAll();
                    }
                    // 阶段一先观察“引入 Spring Security 后默认保护接口”的效果。
                    authorize.anyRequest().authenticated();
                });

        return http.build();
    }
}

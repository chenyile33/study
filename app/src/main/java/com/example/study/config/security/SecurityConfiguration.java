package com.example.study.config.security;

import com.example.common.web.auth.AuthFilter;
import jakarta.annotation.Resource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/**
 * Spring Security API 安全配置。
 *
 * <p>/api/custom-auth/** 保留给 common 自研 AuthFilter 做对照学习；
 * 其他接口默认进入 Spring Security 过滤器链。</p>
 */
// 本配置类没有在 @Bean 方法之间互相调用，关闭代理即可。
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfiguration {

    /**
     * 让 custom-auth 的 common AuthFilter 比普通业务过滤器更早执行，同时给更底层的框架过滤器预留顺序空间。
     */
    private static final int COMMON_AUTH_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 20;

    @Resource
    private BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;

    @Resource
    private SecurityJsonExceptionHandler securityJsonExceptionHandler;

    /**
     * 定义 app 模块自己的 Spring Security 过滤器链，覆盖 custom-auth 之外的接口。
     *
     * <p>登录、注册等白名单接口允许匿名访问；blog、foundation、security demo 等其他接口
     * 统一由 Spring Security 管理。</p>
     */
    @Bean
    public SecurityFilterChain appSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http.securityMatcher(request -> !SecurityPaths.isCustomAuthRequest(request))
                .csrf(AbstractHttpConfigurer::disable)
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
                // 过滤器链不会进入 ControllerAdvice，这里单独接管未登录和权限不足响应。
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(securityJsonExceptionHandler)
                        .accessDeniedHandler(securityJsonExceptionHandler))
                // Bearer token 校验成功后，AuthorizationFilter 才能看到已认证的 Authentication。
                .addFilterBefore(bearerTokenAuthenticationFilter, AuthorizationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SecurityPaths.SECURITY_PERMIT_PATHS).permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    /**
     * 只让 BearerTokenAuthenticationFilter 进入 Spring Security 过滤器链，避免被 Servlet 容器重复执行。
     */
    @Bean
    public FilterRegistrationBean<BearerTokenAuthenticationFilter> bearerTokenAuthenticationFilterRegistration(
            BearerTokenAuthenticationFilter filter) {
        FilterRegistrationBean<BearerTokenAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * common AuthFilter 只负责 /api/custom-auth/**，其余路径交给 Spring Security。
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> commonAuthFilterRegistration(AuthFilter commonAuthFilter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>(commonAuthFilter);
        registration.setName("commonAuthFilter");
        registration.addUrlPatterns(SecurityPaths.CUSTOM_AUTH_FILTER_PATTERN);
        registration.setOrder(COMMON_AUTH_FILTER_ORDER);
        return registration;
    }
}

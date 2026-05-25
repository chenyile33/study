package com.example.study.config.security;

import com.example.common.web.auth.AuthFilter;
import com.example.common.web.auth.CommonAuthProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
 * <p>阶段二接入 Bearer token，并把现有认证主体同步映射到 Spring Security 上下文。
 * 阶段四开始启用方法级安全，用 @PreAuthorize 对照现有授权注解。</p>
 */
// 本配置类没有在 @Bean 方法之间互相调用，关闭代理即可。
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfiguration {

    /**
     * 这里复用之前 common-web 里定义的认证配置。
     *
     * <p>它对应 application.yml 里的 common.auth.*，
     * 阶段一先借用其中的 permit-paths 作为 Spring Security 的匿名访问路径。</p>
     */
    @Resource
    private CommonAuthProperties commonAuthProperties;

    @Resource
    private BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;

    @Resource
    private SecurityJsonExceptionHandler securityJsonExceptionHandler;

    /**
     * 定义 app 模块自己的 Spring Security 过滤器链。
     *
     * <p>登录、注册等白名单接口允许匿名访问；其他接口由 Bearer token 过滤器完成认证。</p>
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
                // 过滤器链不会进入 ControllerAdvice，这里单独接管未登录和权限不足响应。
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(securityJsonExceptionHandler)
                        .accessDeniedHandler(securityJsonExceptionHandler))
                // Bearer token 校验成功后，AuthorizationFilter 才能看到已认证的 Authentication。
                .addFilterBefore(bearerTokenAuthenticationFilter, AuthorizationFilter.class)
                .authorizeHttpRequests(authorize -> {
                    // 登录、JWT 登录、注册等公开接口来自 application.yml 的 common.auth.permit-paths。
                    if (permitPaths.length > 0) {
                        authorize.requestMatchers(permitPaths).permitAll();
                    }
                    if (commonAuthProperties.isEnabled()) {
                        authorize.anyRequest().authenticated();
                    } else {
                        // common.auth.enabled=false 时，app 也不应被 Spring Security 意外拦截。
                        authorize.anyRequest().permitAll();
                    }
                });

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
     * 阶段二开始由 Spring Security 过滤器负责认证，避免 common AuthFilter 再被 Servlet 容器执行。
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> commonAuthFilterRegistration(AuthFilter commonAuthFilter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>(commonAuthFilter);
        registration.setEnabled(false);
        return registration;
    }
}

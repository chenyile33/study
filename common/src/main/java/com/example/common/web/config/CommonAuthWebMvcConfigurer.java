package com.example.common.web.config;

import com.example.common.web.auth.AuthorizationInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * common-auth 对 Spring MVC 的接入点。
 *
 * <p>配置类负责创建 Bean，本类只负责把授权拦截器挂到 MVC 请求链路里。</p>
 */
public class CommonAuthWebMvcConfigurer implements WebMvcConfigurer {

    /**
     * 负责读取 @RequireRoles / @RequirePermissions 并执行授权判断。
     */
    private final AuthorizationInterceptor authorizationInterceptor;

    public CommonAuthWebMvcConfigurer(AuthorizationInterceptor authorizationInterceptor) {
        this.authorizationInterceptor = authorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册到 Spring MVC 后，Controller 方法执行前会先进入 AuthorizationInterceptor。
        registry.addInterceptor(authorizationInterceptor);
    }
}

package com.example.common.web.config;

import com.example.common.core.auth.TokenAuthenticator;
import com.example.common.core.auth.authorization.AuthorizationChecker;
import com.example.common.core.auth.authorization.DefaultAuthorizationChecker;
import com.example.common.web.auth.AuthFilter;
import com.example.common.web.auth.AuthorizationInterceptor;
import com.example.common.web.auth.BearerTokenResolver;
import com.example.common.web.auth.CommonAuthProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * common-web 认证能力的显式装配入口。
 *
 * <p>只有使用 @EnableCommonAuthWeb 时才会引入本配置。</p>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CommonAuthProperties.class)
public class CommonAuthWebConfiguration {

    @Bean
    public BearerTokenResolver bearerTokenResolver(CommonAuthProperties commonAuthProperties) {
        return new BearerTokenResolver(commonAuthProperties);
    }

    @Bean
    public AuthFilter commonAuthFilter(CommonAuthProperties commonAuthProperties,
                                       BearerTokenResolver bearerTokenResolver,
                                       ObjectProvider<TokenAuthenticator> tokenAuthenticatorProvider) {
        TokenAuthenticator tokenAuthenticator = tokenAuthenticatorProvider.getIfAvailable();
        // 认证启用时必须有 token 校验实现；关闭时允许应用暂时不提供。
        if (commonAuthProperties.isEnabled() && tokenAuthenticator == null) {
            throw new IllegalStateException("common auth is enabled, but TokenAuthenticator bean is missing");
        }
        return new AuthFilter(commonAuthProperties, bearerTokenResolver, tokenAuthenticator);
    }

    @Bean
    public AuthorizationInterceptor commonAuthorizationInterceptor(
            CommonAuthProperties commonAuthProperties,
            ObjectProvider<AuthorizationChecker> authorizationCheckerProvider) {
        // 允许业务项目提供自己的 AuthorizationChecker；没有提供时使用 AuthPrincipal 快照判断。
        AuthorizationChecker authorizationChecker = authorizationCheckerProvider
                .getIfAvailable(DefaultAuthorizationChecker::new);
        return new AuthorizationInterceptor(commonAuthProperties, authorizationChecker);
    }

    @Bean
    public CommonAuthWebMvcConfigurer commonAuthWebMvcConfigurer(AuthorizationInterceptor authorizationInterceptor) {
        return new CommonAuthWebMvcConfigurer(authorizationInterceptor);
    }
}

package com.example.common.core.auth.authorization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明访问当前类或方法需要具备的权限码。
 *
 * <p>权限码通常表示具体操作，例如 user:disable、blog:delete，适合真实业务中的细粒度控制。</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermissions {

    /**
     * 需要的权限编码，例如 blog:create、user:disable。
     */
    String[] value();

    /**
     * 多个权限之间的匹配方式，默认满足任意一个即可。
     */
    AuthorizationMode mode() default AuthorizationMode.ANY;
}

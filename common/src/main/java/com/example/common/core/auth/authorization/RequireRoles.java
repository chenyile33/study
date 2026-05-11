package com.example.common.core.auth.authorization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明访问当前类或方法需要具备的角色。
 *
 * <p>角色通常表示身份分组，例如 ADMIN、USER；岗位调整频繁的业务权限更适合用 RequirePermissions。</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRoles {

    /**
     * 需要的角色编码，例如 ADMIN、USER。
     */
    String[] value();

    /**
     * 多个角色之间的匹配方式，默认满足任意一个即可。
     */
    AuthorizationMode mode() default AuthorizationMode.ANY;
}

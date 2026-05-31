package com.example.common.web;

import com.example.common.web.config.CommonAuthWebConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 显式启用 common-web 的认证过滤能力。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CommonAuthWebConfiguration.class)
public @interface EnableCommonAuthWeb {
}

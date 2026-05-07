package com.example.common.web;

import com.example.common.web.config.CommonWebConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 显式启用 common 提供的 Web 通用能力。
 *
 * <p>@Import 会把 CommonWebConfiguration 加入当前 Spring 容器，
 * 比组件扫描更明确，适合这种默认不自动生效的公共能力。</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CommonWebConfiguration.class)
public @interface EnableCommonWeb {
}

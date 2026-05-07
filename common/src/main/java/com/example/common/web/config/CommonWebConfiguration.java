package com.example.common.web.config;

import com.example.common.web.exception.GlobalExceptionHandler;
import com.example.common.web.trace.TraceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * common-web 的显式装配入口，由 @EnableCommonWeb 引入。
 *
 * <p>proxyBeanMethods=false 表示 Spring 不需要代理本配置类里的 @Bean 方法。
 * 这里的 Bean 之间没有互相调用，关闭代理更简单，也避免读代码时误以为有特殊生命周期逻辑。</p>
 */
@Configuration(proxyBeanMethods = false)
public class CommonWebConfiguration {

    @Bean
    public TraceFilter commonTraceFilter() {
        return new TraceFilter();
    }

    @Bean
    public GlobalExceptionHandler commonGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}

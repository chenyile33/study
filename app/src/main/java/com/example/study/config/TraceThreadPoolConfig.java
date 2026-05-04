package com.example.study.config;

import com.example.common.spring.trace.TraceTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 演示如何把 trace 传递能力接入 Spring 线程池。
 */
@Configuration
public class TraceThreadPoolConfig {

    @Bean(name = "traceTaskExecutor")
    public ThreadPoolTaskExecutor traceTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("trace-task-");
        executor.setTaskDecorator(new TraceTaskDecorator());
        executor.initialize();
        return executor;
    }
}

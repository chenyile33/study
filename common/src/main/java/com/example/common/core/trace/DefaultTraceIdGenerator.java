package com.example.common.core.trace;

import java.util.UUID;

/**
 * 默认 traceId 生成器。
 *
 * <p>生成 32 位 UUID 字符串，不带中划线，适合日志检索和请求头传递。</p>
 */
public final class DefaultTraceIdGenerator implements TraceIdGenerator {

    /**
     * 默认单例，避免每次生成 traceId 都创建对象。
     */
    public static final DefaultTraceIdGenerator INSTANCE = new DefaultTraceIdGenerator();

    private DefaultTraceIdGenerator() {
    }

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

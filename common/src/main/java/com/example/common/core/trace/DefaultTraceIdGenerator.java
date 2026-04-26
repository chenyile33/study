package com.example.common.core.trace;

import java.util.UUID;

/**
 * 默认 traceId 生成器，使用 32 位 UUID 字符串。
 */
public final class DefaultTraceIdGenerator implements TraceIdGenerator {

    public static final DefaultTraceIdGenerator INSTANCE = new DefaultTraceIdGenerator();

    private DefaultTraceIdGenerator() {
    }

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

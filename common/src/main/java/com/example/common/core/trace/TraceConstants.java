package com.example.common.core.trace;

/**
 * trace 相关常量，统一请求头和日志上下文里的 key。
 */
public final class TraceConstants {

    public static final String TRACE_ID = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private TraceConstants() {
    }
}

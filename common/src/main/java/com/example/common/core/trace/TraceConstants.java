package com.example.common.core.trace;

/**
 * trace 常量。
 *
 * <p>统一请求头、MDC、上下文里使用的 key，避免各处手写字符串。</p>
 */
public final class TraceConstants {

    /**
     * MDC 中保存 traceId 的 key，对应日志格式里的 %X{traceId}。
     */
    public static final String TRACE_ID = "traceId";

    /**
     * HTTP 请求头和响应头里的 traceId 名称。
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private TraceConstants() {
    }
}

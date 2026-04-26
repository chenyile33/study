package com.example.common.core.trace;

/**
 * 当前线程的 trace 上下文。
 *
 * <p>这里不依赖 Spring 或日志框架，Web、MQ、任务调度等模块只负责在边界处写入和清理 traceId。</p>
 */
public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();

    private TraceContext() {
    }

    public static String getTraceId() {
        return TRACE_ID_HOLDER.get();
    }

    public static String getOrCreateTraceId() {
        return getOrCreateTraceId(DefaultTraceIdGenerator.INSTANCE);
    }

    public static String getOrCreateTraceId(TraceIdGenerator traceIdGenerator) {
        String traceId = getTraceId();
        if (hasText(traceId)) {
            return traceId;
        }

        TraceIdGenerator generator = traceIdGenerator == null ? DefaultTraceIdGenerator.INSTANCE : traceIdGenerator;
        traceId = generator.generate();
        setTraceId(traceId);
        return traceId;
    }

    public static void setTraceId(String traceId) {
        if (!hasText(traceId)) {
            clear();
            return;
        }
        TRACE_ID_HOLDER.set(traceId.trim());
    }

    /**
     * 打开一个 trace 作用域，适合在拦截器、消费者、任务入口中配合 try-with-resources 使用。
     */
    public static TraceScope open(String traceId) {
        String previousTraceId = getTraceId();
        if (hasText(traceId)) {
            setTraceId(traceId);
        } else {
            getOrCreateTraceId();
        }
        return new TraceScope(previousTraceId);
    }

    public static TraceScope open() {
        String previousTraceId = getTraceId();
        getOrCreateTraceId();
        return new TraceScope(previousTraceId);
    }

    public static void clear() {
        TRACE_ID_HOLDER.remove();
    }

    static void restore(String traceId) {
        if (hasText(traceId)) {
            TRACE_ID_HOLDER.set(traceId);
        } else {
            clear();
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

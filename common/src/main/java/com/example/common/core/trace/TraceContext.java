package com.example.common.core.trace;

/**
 * 当前线程的 trace 上下文。
 *
 * <p>底层使用 ThreadLocal，所以 traceId 只在当前线程内有效。Web、异步任务、后台任务等入口负责写入和清理。</p>
 * <p>本类不依赖 Spring 或日志框架，可以被任何 common 能力复用。</p>
 */
public final class TraceContext {

    /**
     * 每个线程都有自己独立的 traceId，避免并发请求互相覆盖。
     */
    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();

    private TraceContext() {
    }

    public static String getTraceId() {
        return TRACE_ID_HOLDER.get();
    }

    /**
     * 获取当前 traceId；如果不存在，则用默认生成器创建一个。
     */
    public static String getOrCreateTraceId() {
        return getOrCreateTraceId(DefaultTraceIdGenerator.INSTANCE);
    }

    /**
     * 获取当前 traceId；如果不存在，则用指定生成器创建一个。
     */
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

    /**
     * 设置当前线程的 traceId。空字符串会被当成清理操作。
     */
    public static void setTraceId(String traceId) {
        if (!hasText(traceId)) {
            clear();
            return;
        }
        TRACE_ID_HOLDER.set(traceId.trim());
    }

    /**
     * 打开一个 trace 作用域，适合在拦截器、消费者、任务入口中配合 try-with-resources 使用。
     *
     * <p>传入为空时自动生成 traceId；作用域关闭时会恢复之前的 traceId。</p>
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

    /**
     * 打开一个自动生成 traceId 的作用域。
     */
    public static TraceScope open() {
        String previousTraceId = getTraceId();
        getOrCreateTraceId();
        return new TraceScope(previousTraceId);
    }

    /**
     * 清理当前线程的 traceId。线程池场景必须清理，避免污染下一个任务。
     */
    public static void clear() {
        TRACE_ID_HOLDER.remove();
    }

    /**
     * 恢复旧 traceId，仅供 TraceScope 关闭时调用。
     */
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

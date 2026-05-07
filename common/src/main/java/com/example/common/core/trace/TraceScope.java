package com.example.common.core.trace;

/**
 * trace 作用域。
 *
 * <p>配合 try-with-resources 使用，关闭时自动恢复进入作用域前的 traceId。</p>
 *
 * <pre>
 * try (TraceScope ignored = TraceContext.open(traceId)) {
 *     // 当前代码块内可以读取 traceId
 * }
 * </pre>
 */
public final class TraceScope implements AutoCloseable {

    private final String previousTraceId;
    private boolean closed;

    TraceScope(String previousTraceId) {
        this.previousTraceId = previousTraceId;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        // 恢复旧值，避免线程池复用时把本次 traceId 留给下一次任务。
        TraceContext.restore(previousTraceId);
        closed = true;
    }
}

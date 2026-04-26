package com.example.common.core.trace;

/**
 * trace 作用域，关闭时恢复进入作用域前的 traceId。
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
        TraceContext.restore(previousTraceId);
        closed = true;
    }
}

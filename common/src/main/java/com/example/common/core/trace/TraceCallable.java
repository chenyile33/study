package com.example.common.core.trace;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 带 trace 的 Callable 包装器。
 *
 * <p>作用与 TraceRunnable 相同，但适用于需要返回值或抛出异常的异步任务。</p>
 */
public final class TraceCallable<V> implements Callable<V> {

    private final String traceId;
    private final Callable<V> delegate;

    private TraceCallable(String traceId, Callable<V> delegate) {
        this.traceId = traceId;
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    public static <V> Callable<V> wrap(Callable<V> delegate) {
        return wrap(TraceContext.getTraceId(), delegate);
    }

    /**
     * 使用指定 traceId 包装任务，适合从请求头、消息头等地方显式传入。
     */
    public static <V> Callable<V> wrap(String traceId, Callable<V> delegate) {
        return new TraceCallable<>(traceId, delegate);
    }

    @Override
    public V call() throws Exception {
        // 在线程池线程中重新打开 trace 作用域，任务结束后自动恢复。
        try (TraceScope ignored = TraceContext.open(traceId)) {
            return delegate.call();
        }
    }
}

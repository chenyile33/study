package com.example.common.core.trace;

import java.util.Objects;

/**
 * 带 trace 的 Runnable 包装器。
 *
 * <p>用于手动提交异步任务时，把提交线程的 traceId 传到执行线程。</p>
 *
 * <pre>
 * executor.submit(TraceRunnable.wrap(() -> log.info("异步任务")));
 * </pre>
 */
public final class TraceRunnable implements Runnable {

    private final String traceId;
    private final Runnable delegate;

    private TraceRunnable(String traceId, Runnable delegate) {
        this.traceId = traceId;
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    public static Runnable wrap(Runnable delegate) {
        return wrap(TraceContext.getTraceId(), delegate);
    }

    /**
     * 使用指定 traceId 包装任务，适合从请求头、消息头等地方显式传入。
     */
    public static Runnable wrap(String traceId, Runnable delegate) {
        return new TraceRunnable(traceId, delegate);
    }

    @Override
    public void run() {
        // 在线程池线程中重新打开 trace 作用域，任务结束后自动恢复。
        try (TraceScope ignored = TraceContext.open(traceId)) {
            delegate.run();
        }
    }
}

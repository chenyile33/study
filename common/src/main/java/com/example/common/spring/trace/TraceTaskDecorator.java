package com.example.common.spring.trace;

import com.example.common.core.trace.TraceConstants;
import com.example.common.core.trace.TraceContext;
import com.example.common.core.trace.TraceScope;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;

/**
 * Spring 线程池任务装饰器。
 *
 * <p>用于把提交任务线程里的 TraceContext 和 MDC 复制到线程池执行线程。</p>
 * <p>这是普通工具类，不会自动注册为 Spring Bean。业务项目需要显式配置到 ThreadPoolTaskExecutor 才会生效。</p>
 */
public class TraceTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(@NonNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable must not be null");

        // decorate 在提交任务的线程执行，此时要捕获父线程上下文。
        String traceId = TraceContext.getTraceId();
        Map<String, String> parentMdcContext = MDC.getCopyOfContextMap();

        return () -> {
            // run 在线程池线程执行，先保存旧上下文，避免覆盖外层任务。
            Map<String, String> previousMdcContext = MDC.getCopyOfContextMap();

            try (TraceScope ignored = TraceContext.open(traceId)) {
                restoreMdcContext(parentMdcContext);
                MDC.put(TraceConstants.TRACE_ID, TraceContext.getTraceId());
                runnable.run();
            } finally {
                // 线程池线程会复用，任务结束必须恢复旧 MDC。
                restoreMdcContext(previousMdcContext);
            }
        };
    }

    private static void restoreMdcContext(Map<String, String> contextMap) {
        if (contextMap == null || contextMap.isEmpty()) {
            MDC.clear();
            return;
        }
        MDC.setContextMap(contextMap);
    }
}

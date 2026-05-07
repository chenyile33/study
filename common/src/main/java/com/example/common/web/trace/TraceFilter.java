package com.example.common.web.trace;

import com.example.common.core.trace.TraceConstants;
import com.example.common.core.trace.TraceContext;
import com.example.common.core.trace.TraceScope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 为每个 Web 请求准备 traceId，并写入响应头和日志 MDC。
 *
 * <p>过滤器是 Web 层 trace 的入口；核心 trace 状态仍然由 common-core 的 TraceContext 管理。</p>
 */
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestTraceId = request.getHeader(TraceConstants.TRACE_ID_HEADER);
        String previousMdcTraceId = MDC.get(TraceConstants.TRACE_ID);

        try (TraceScope ignored = TraceContext.open(requestTraceId)) {
            String traceId = TraceContext.getTraceId();
            MDC.put(TraceConstants.TRACE_ID, traceId);
            response.setHeader(TraceConstants.TRACE_ID_HEADER, traceId);

            filterChain.doFilter(request, response);
        } finally {
            restoreMdcTraceId(previousMdcTraceId);
        }
    }

    /**
     * 恢复进入过滤器前的 MDC，避免线程复用时串 traceId。
     */
    private void restoreMdcTraceId(String previousMdcTraceId) {
        if (previousMdcTraceId == null) {
            MDC.remove(TraceConstants.TRACE_ID);
            return;
        }
        MDC.put(TraceConstants.TRACE_ID, previousMdcTraceId);
    }
}

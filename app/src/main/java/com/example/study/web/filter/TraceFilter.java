package com.example.study.web.filter;

import com.example.common.core.trace.TraceConstants;
import com.example.common.core.trace.TraceContext;
import com.example.common.core.trace.TraceScope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 为每个 Web 请求准备 traceId，并写入日志 MDC。
 */
@Component
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
     * 兼容同一线程里已有 MDC traceId 的场景，避免直接清空外层上下文。
     */
    private void restoreMdcTraceId(String previousMdcTraceId) {
        if (previousMdcTraceId == null) {
            MDC.remove(TraceConstants.TRACE_ID);
            return;
        }
        MDC.put(TraceConstants.TRACE_ID, previousMdcTraceId);
    }
}

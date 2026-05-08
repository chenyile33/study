package com.example.study.demo.commoncore;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.result.Result;
import com.example.common.core.trace.TraceContext;
import com.example.common.core.util.AssertUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 用于验证 common-core 统一返回和业务异常链路。
 */
@RestController
@RequestMapping("/api/common-core")
public class CommonCoreDemoController {

    private static final Logger log = LoggerFactory.getLogger(CommonCoreDemoController.class);

    @Resource(name = "traceTaskExecutor")
    private ThreadPoolTaskExecutor traceTaskExecutor;

    @GetMapping("/success")
    public Result<String> success(@RequestParam(defaultValue = "study") String name) {
        log.info("访问 common-core success 接口，name={}", name);
        return Result.success("hello, " + name);
    }

    @GetMapping("/assert")
    public Result<String> assertName(@RequestParam(required = false) String name) {
        AssertUtils.hasText(name, CommonErrorCode.PARAM_ERROR, "name不能为空");
        log.info("访问 common-core assert 接口，name={}", name);
        return Result.success("hello, " + name.trim());
    }

    @GetMapping("/trace")
    public Result<String> trace() {
        String traceId = TraceContext.getOrCreateTraceId();
        log.info("访问 common-core trace 接口");
        return Result.success(traceId);
    }

    @GetMapping("/trace-async")
    public Result<String> traceAsync() {
        String mainTraceId = TraceContext.getTraceId();
        log.info("主线程提交异步任务");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.info("异步线程执行任务");
            return TraceContext.getTraceId();
        }, traceTaskExecutor);

        try {
            String asyncTraceId = future.get(3, TimeUnit.SECONDS);
            log.info("异步任务执行完成");
            return Result.success("main=" + mainTraceId + ", async=" + asyncTraceId);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BusinessException(CommonErrorCode.SYSTEM_ERROR, "异步任务被中断", exception);
        } catch (ExecutionException | TimeoutException exception) {
            throw new BusinessException(CommonErrorCode.SYSTEM_ERROR, "异步任务执行失败", exception);
        }
    }

    @GetMapping("/business-error")
    public Result<Void> businessError() {
        log.info("访问 common-core business-error 接口");
        throw new BusinessException(CommonErrorCode.PARAM_ERROR, "演示业务异常");
    }
}

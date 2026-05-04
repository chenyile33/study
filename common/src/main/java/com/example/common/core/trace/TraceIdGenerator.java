package com.example.common.core.trace;

/**
 * traceId 生成器。
 *
 * <p>默认用 UUID。后续如果需要业务前缀、雪花算法，可以替换这个接口的实现。</p>
 */
@FunctionalInterface
public interface TraceIdGenerator {

    /**
     * 生成一个新的 traceId。
     */
    String generate();
}

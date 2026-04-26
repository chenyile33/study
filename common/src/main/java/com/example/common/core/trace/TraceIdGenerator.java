package com.example.common.core.trace;

/**
 * traceId 生成器抽象，后续可以替换成雪花算法、业务前缀等实现。
 */
@FunctionalInterface
public interface TraceIdGenerator {

    String generate();
}

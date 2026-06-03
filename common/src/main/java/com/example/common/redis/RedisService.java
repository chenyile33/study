package com.example.common.redis;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Redis 常用操作入口。
 *
 * <p>这里不是封装全部 Redis 命令，而是保留业务代码里高频、容易复用的基础能力。
 * 对象存储使用 JSON 字符串，具体 key 是否代表对象缓存，由 key 命名和调用方约定决定。</p>
 */
public interface RedisService {

    void set(String key, String value);

    void set(String key, String value, Duration ttl);

    Optional<String> get(String key);

    boolean delete(String key);

    boolean expire(String key, Duration ttl);

    boolean setIfAbsent(String key, String value);

    boolean setIfAbsent(String key, String value, Duration ttl);

    Long increment(String key);

    Long increment(String key, long delta);

    void setObject(String key, Object value);

    void setObject(String key, Object value, Duration ttl);

    <T> Optional<T> getObject(String key, Class<T> type);

    void hSet(String key, String hashKey, String value);

    Optional<String> hGet(String key, String hashKey);

    Long hDelete(String key, String... hashKeys);

    Map<String, String> hEntries(String key);

    Long lLeftPush(String key, String value);

    Long lRightPush(String key, String value);

    Optional<String> lLeftPop(String key);

    Optional<String> lRightPop(String key);

    List<String> lRange(String key, long start, long end);

    Long sAdd(String key, String... values);

    Long sRemove(String key, String... values);

    Set<String> sMembers(String key);

    boolean zAdd(String key, String value, double score);

    Long zRemove(String key, String... values);

    Set<String> zRange(String key, long start, long end);
}

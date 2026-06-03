package com.example.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 基于 StringRedisTemplate 的 Redis 常用操作实现。
 */
public class DefaultRedisService implements RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public DefaultRedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(requireText(key, "key"), requireNonNull(value, "value"));
    }

    @Override
    public void set(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(requireText(key, "key"), requireNonNull(value, "value"),
                requirePositive(ttl, "ttl"));
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(requireText(key, "key")));
    }

    @Override
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(requireText(key, "key")));
    }

    @Override
    public boolean expire(String key, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.expire(requireText(key, "key"), requirePositive(ttl, "ttl")));
    }

    @Override
    public boolean setIfAbsent(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(requireText(key, "key"), requireNonNull(value, "value")));
    }

    @Override
    public boolean setIfAbsent(String key, String value, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(requireText(key, "key"), requireNonNull(value, "value"),
                        requirePositive(ttl, "ttl")));
    }

    @Override
    public Long increment(String key) {
        return increment(key, 1L);
    }

    @Override
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(requireText(key, "key"), delta);
    }

    @Override
    public void setObject(String key, Object value) {
        set(key, toJson(value));
    }

    @Override
    public void setObject(String key, Object value, Duration ttl) {
        set(key, toJson(value), ttl);
    }

    @Override
    public <T> Optional<T> getObject(String key, Class<T> type) {
        requireNonNull(type, "type");
        return get(key).map(value -> fromJson(value, type));
    }

    @Override
    public void hSet(String key, String hashKey, String value) {
        hashOperations().put(requireText(key, "key"), requireText(hashKey, "hashKey"),
                requireNonNull(value, "value"));
    }

    @Override
    public Optional<String> hGet(String key, String hashKey) {
        return Optional.ofNullable(hashOperations().get(requireText(key, "key"), requireText(hashKey, "hashKey")));
    }

    @Override
    public Long hDelete(String key, String... hashKeys) {
        return hashOperations().delete(requireText(key, "key"), (Object[]) requireValues(hashKeys, "hashKeys"));
    }

    @Override
    public Map<String, String> hEntries(String key) {
        return hashOperations().entries(requireText(key, "key"));
    }

    @Override
    public Long lLeftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(requireText(key, "key"), requireNonNull(value, "value"));
    }

    @Override
    public Long lRightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(requireText(key, "key"), requireNonNull(value, "value"));
    }

    @Override
    public Optional<String> lLeftPop(String key) {
        return Optional.ofNullable(redisTemplate.opsForList().leftPop(requireText(key, "key")));
    }

    @Override
    public Optional<String> lRightPop(String key) {
        return Optional.ofNullable(redisTemplate.opsForList().rightPop(requireText(key, "key")));
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        List<String> values = redisTemplate.opsForList().range(requireText(key, "key"), start, end);
        return values == null ? Collections.emptyList() : values;
    }

    @Override
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(requireText(key, "key"), requireValues(values, "values"));
    }

    @Override
    public Long sRemove(String key, String... values) {
        return redisTemplate.opsForSet().remove(requireText(key, "key"), (Object[]) requireValues(values, "values"));
    }

    @Override
    public Set<String> sMembers(String key) {
        Set<String> values = redisTemplate.opsForSet().members(requireText(key, "key"));
        return values == null ? Collections.emptySet() : values;
    }

    @Override
    public boolean zAdd(String key, String value, double score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet()
                .add(requireText(key, "key"), requireNonNull(value, "value"), score));
    }

    @Override
    public Long zRemove(String key, String... values) {
        return redisTemplate.opsForZSet().remove(requireText(key, "key"), (Object[]) requireValues(values, "values"));
    }

    @Override
    public Set<String> zRange(String key, long start, long end) {
        Set<String> values = redisTemplate.opsForZSet().range(requireText(key, "key"), start, end);
        return values == null ? Collections.emptySet() : values;
    }

    private HashOperations<String, String, String> hashOperations() {
        return redisTemplate.opsForHash();
    }

    private String toJson(Object value) {
        requireNonNull(value, "value");
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("value must be serializable to JSON", exception);
        }
    }

    private <T> T fromJson(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("value is not valid JSON for " + type.getName(), exception);
        }
    }

    private static String requireText(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    private static <T> T requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

    private static Duration requirePositive(Duration value, String name) {
        if (value == null || value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }

    private static String[] requireValues(String[] values, String name) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        String[] normalizedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            normalizedValues[i] = requireText(values[i], name);
        }
        return normalizedValues;
    }
}

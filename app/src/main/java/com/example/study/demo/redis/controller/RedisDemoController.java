package com.example.study.demo.redis.controller;

import com.example.common.core.result.Result;
import com.example.common.redis.RedisService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 基础操作 Demo。
 *
 * <p>Controller 只演示业务入口，Redis 的基础命令统一走 common 的 RedisService。
 * 后续分布式锁、缓存等能力可以继续基于这个入口往上封装。</p>
 */
@Validated
@RestController
@RequestMapping("/api/redis")
public class RedisDemoController {

    @Resource
    private RedisService redisService;

    @PostMapping("/strings")
    public Result<RedisValueResponse> setString(@Valid @RequestBody RedisStringRequest request) {
        if (request.ttlSeconds() == null) {
            redisService.set(request.key(), request.value());
        } else {
            redisService.set(request.key(), request.value(), Duration.ofSeconds(request.ttlSeconds()));
        }
        return Result.success(new RedisValueResponse(request.key(), request.value(), true));
    }

    @GetMapping("/strings/{key}")
    public Result<RedisValueResponse> getString(@PathVariable @NotBlank(message = "key不能为空") String key) {
        String value = redisService.get(key).orElse(null);
        return Result.success(new RedisValueResponse(key, value, value != null));
    }

    @PostMapping("/counters/{key}/increment")
    public Result<RedisCounterResponse> increment(
            @PathVariable @NotBlank(message = "key不能为空") String key,
            @RequestParam(defaultValue = "1") long delta
    ) {
        Long value = redisService.increment(key, delta);
        return Result.success(new RedisCounterResponse(key, value));
    }

    @PostMapping("/objects/users")
    public Result<RedisObjectResponse<UserCacheValue>> setUserCache(@Valid @RequestBody RedisUserCacheRequest request) {
        String key = "demo:redis:user:" + request.userId();
        UserCacheValue value = new UserCacheValue(request.userId(), request.nickname(), LocalDateTime.now());
        if (request.ttlSeconds() == null) {
            redisService.setObject(key, value);
        } else {
            redisService.setObject(key, value, Duration.ofSeconds(request.ttlSeconds()));
        }
        UserCacheValue cacheValue = redisService.getObject(key, UserCacheValue.class).orElse(null);
        return Result.success(new RedisObjectResponse<>(key, cacheValue, cacheValue != null));
    }

    @PostMapping("/structures/demo")
    public Result<RedisStructuresResponse> writeStructures(
            @RequestParam(defaultValue = "demo:redis") @NotBlank(message = "keyPrefix不能为空") String keyPrefix
    ) {
        String normalizedPrefix = keyPrefix.trim();
        String hashKey = normalizedPrefix + ":hash";
        String listKey = normalizedPrefix + ":list";
        String setKey = normalizedPrefix + ":set";
        String zSetKey = normalizedPrefix + ":zset";

        redisService.delete(hashKey);
        redisService.delete(listKey);
        redisService.delete(setKey);
        redisService.delete(zSetKey);

        redisService.hSet(hashKey, "name", "redis");
        redisService.hSet(hashKey, "type", "hash");
        redisService.lRightPush(listKey, "list-1");
        redisService.lRightPush(listKey, "list-2");
        redisService.lRightPush(listKey, "list-3");
        redisService.sAdd(setKey, "set-1", "set-2", "set-2");
        redisService.zAdd(zSetKey, "zset-1", 1D);
        redisService.zAdd(zSetKey, "zset-2", 2D);

        return Result.success(new RedisStructuresResponse(
                hashKey,
                redisService.hEntries(hashKey),
                listKey,
                redisService.lRange(listKey, 0, -1),
                setKey,
                redisService.sMembers(setKey),
                zSetKey,
                redisService.zRange(zSetKey, 0, -1)
        ));
    }

    public record RedisStringRequest(
            @NotBlank(message = "key不能为空")
            String key,
            @NotNull(message = "value不能为空")
            String value,
            @Positive(message = "ttlSeconds必须大于0")
            Long ttlSeconds
    ) {
    }

    public record RedisUserCacheRequest(
            @NotNull(message = "userId不能为空")
            Long userId,
            @NotBlank(message = "nickname不能为空")
            String nickname,
            @Positive(message = "ttlSeconds必须大于0")
            Long ttlSeconds
    ) {
    }

    public record UserCacheValue(Long userId, String nickname, LocalDateTime cachedAt) {
    }

    public record RedisValueResponse(String key, String value, boolean exists) {
    }

    public record RedisCounterResponse(String key, Long value) {
    }

    public record RedisObjectResponse<T>(String key, T value, boolean exists) {
    }

    public record RedisStructuresResponse(
            String hashKey,
            Map<String, String> hashValue,
            String listKey,
            List<String> listValue,
            String setKey,
            Set<String> setValue,
            String zSetKey,
            Set<String> zSetValue
    ) {
    }
}

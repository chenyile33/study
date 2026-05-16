package com.example.study.demo.auth.jwt;

import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JWT 版本 token 示例：token 自身携带主体、角色和权限快照，并通过 HMAC 签名防篡改。
 */
@Service
public class JwtTokenService {

    // JCA 的算法名称，用来创建 Mac；JWT header 里对外声明的算法名对应是 HS256。
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    /**
     * 复用 Spring Boot 默认 ObjectMapper，避免手写 JSON 拼接导致转义错误。
     */
    private final ObjectMapper objectMapper;

    /**
     * JWT 签名密钥和有效期配置。
     */
    private final DemoJwtProperties properties;

    /**
     * 抽出 Clock 是为了让过期时间相关逻辑在单元测试里可控。
     */
    private final Clock clock;

    @Autowired
    public JwtTokenService(ObjectMapper objectMapper, DemoJwtProperties properties) {
        this(objectMapper, properties, Clock.systemDefaultZone());
    }

    /**
     * 测试专用构造器，用固定 Clock 验证 JWT 过期逻辑。
     */
    JwtTokenService(ObjectMapper objectMapper, DemoJwtProperties properties, Clock clock) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.clock = clock;
    }

    public JwtToken create(AuthPrincipal principal) {
        Duration ttl = Duration.ofSeconds(Math.max(properties.getTtlSeconds(), 1));
        return create(principal, ttl);
    }

    /**
     * 签发 JWT：header 和 payload 只做 Base64URL 编码，真正防篡改的是 signature。
     */
    public JwtToken create(AuthPrincipal principal, Duration ttl) {
        Objects.requireNonNull(principal, "principal must not be null");
        Objects.requireNonNull(ttl, "ttl must not be null");
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(ttl);

        // alg 和 typ 是 JWT header 的约定字段；HS256/JWT 是本 demo 对验证方声明的算法和 token 类型。
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        // sub、iat、exp 是 JWT 注册声明；name 是用户名快照；roles、permissions、attributes 是本 demo 的自定义声明。
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", principal.getPrincipalId());
        payload.put("name", principal.getPrincipalName());
        payload.put("roles", principal.getRoles());
        payload.put("permissions", principal.getPermissions());
        payload.put("attributes", principal.getAttributes());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        String signingInput = encodeJson(header) + "." + encodeJson(payload);
        String token = signingInput + "." + sign(signingInput);
        LocalDateTime localExpiresAt = LocalDateTime.ofInstant(expiresAt, clock.getZone());
        return new JwtToken(token, principal, localExpiresAt);
    }

    /**
     * 校验 JWT 时必须先验签，再信任 payload 中的主体、角色和权限。
     */
    public AuthPrincipal authenticate(String token) {
        try {
            String[] parts = splitToken(token);
            String signingInput = parts[0] + "." + parts[1];
            verifySignature(signingInput, parts[2]);

            Map<String, Object> header = decodeJson(parts[0]);
            verifyHeader(header);

            Map<String, Object> payload = decodeJson(parts[1]);
            verifyExpiresAt(payload);

            // 读取 payload 时字段名必须和签发时保持一致；sub 表示这个 token 代表哪个认证主体。
            String principalId = requiredText(payload.get("sub"));
            String principalName = optionalText(payload.get("name"));
            List<String> roles = stringList(payload.get("roles"));
            List<String> permissions = stringList(payload.get("permissions"));
            Map<String, String> attributes = stringMap(payload.get("attributes"));
            return AuthPrincipal.of(principalId, principalName, roles, permissions, attributes);
        } catch (AuthException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT无效", exception);
        }
    }

    /**
     * AuthFilter 会同时处理 opaque token 和 JWT；这里用三段式结构识别 JWT。
     */
    public boolean supports(String token) {
        return token != null && token.chars().filter(value -> value == '.').count() == 2;
    }

    private String[] splitToken(String token) {
        // 三段式只能说明“长得像 JWT”，真正可信还要经过后续验签和过期时间校验。
        if (!supports(token)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT格式错误");
        }
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3 || isBlank(parts[0]) || isBlank(parts[1]) || isBlank(parts[2])) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT格式错误");
        }
        return parts;
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(value);
            return BASE64_URL_ENCODER.encodeToString(json);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("JWT JSON序列化失败", exception);
        }
    }

    private Map<String, Object> decodeJson(String value) {
        try {
            byte[] json = BASE64_URL_DECODER.decode(value);
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (IllegalArgumentException | IOException exception) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT载荷无效", exception);
        }
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secretBytes(), HMAC_SHA256));
            byte[] signature = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
            return BASE64_URL_ENCODER.encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("JWT签名失败", exception);
        }
    }

    private void verifySignature(String signingInput, String actualSignature) {
        String expectedSignature = sign(signingInput);
        // 使用常量时间比较，避免签名比较过程泄露过多差异信息。
        boolean matched = MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.US_ASCII),
                actualSignature.getBytes(StandardCharsets.US_ASCII)
        );
        if (!matched) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT签名无效");
        }
    }

    private void verifyHeader(Map<String, Object> header) {
        String algorithm = optionalText(header.get("alg"));
        String tokenType = optionalText(header.get("typ"));
        // 不能让客户端传来的 alg 决定验签算法；这里只确认它和本地固定的 HS256 实现一致。
        if (!"HS256".equals(algorithm) || !"JWT".equalsIgnoreCase(tokenType)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT头部无效");
        }
    }

    private void verifyExpiresAt(Map<String, Object> payload) {
        long expiresAt = requiredNumber(payload.get("exp"));
        if (Instant.now(clock).getEpochSecond() >= expiresAt) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT已过期");
        }
    }

    private byte[] secretBytes() {
        String secret = properties.getSecret();
        if (isBlank(secret)) {
            throw new IllegalStateException("demo.auth.jwt.secret must not be blank");
        }
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("demo.auth.jwt.secret must be at least 32 bytes");
        }
        return bytes;
    }

    private static String requiredText(Object value) {
        String text = optionalText(value);
        if (isBlank(text)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT缺少subject");
        }
        return text;
    }

    private static String optionalText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static long requiredNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new AuthException(AuthErrorCode.UNAUTHORIZED, "JWT缺少过期时间");
    }

    private static List<String> stringList(Object value) {
        // Jackson 反序列化 JWT payload 后只能先拿到 Object，这里把角色/权限声明规范化成字符串列表。
        if (!(value instanceof Collection<?> values)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        for (Object item : values) {
            String text = optionalText(item);
            if (!isBlank(text)) {
                result.add(text);
            }
        }
        return result;
    }

    private static Map<String, String> stringMap(Object value) {
        // attributes 是 demo 自定义的字符串扩展信息，读取时统一转成 Map<String, String>。
        if (!(value instanceof Map<?, ?> values)) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            String key = optionalText(entry.getKey());
            if (!isBlank(key)) {
                result.put(key, optionalText(entry.getValue()));
            }
        }
        return result;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

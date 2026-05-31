package com.example.common.core.auth;

/**
 * token 认证接口。
 *
 * <p>接入项目负责实现具体 token 校验逻辑，common 只依赖这个稳定接口。</p>
 * <p>典型实现可以查询内存、Redis、数据库，或校验 JWT 签名。</p>
 */
@FunctionalInterface
public interface TokenAuthenticator {

    /**
     * 校验 token，并返回认证主体。
     *
     * <p>认证成功必须返回非 null 主体；认证失败应抛出 AuthException。</p>
     *
     * @throws AuthException token 无效、过期或无权访问时抛出
     */
    AuthPrincipal authenticate(String token);
}

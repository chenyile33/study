package com.example.study.demo.auth.service;

import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.TokenAuthenticator;
import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.util.AssertUtils;
import com.example.study.demo.auth.domain.DemoAccount;
import com.example.study.demo.auth.domain.StoredToken;
import com.example.study.demo.auth.dto.LoginRequest;
import com.example.study.demo.auth.dto.LoginResponse;
import com.example.study.demo.auth.jwt.JwtToken;
import com.example.study.demo.auth.jwt.JwtTokenService;
import com.example.study.demo.auth.password.PasswordHasher;
import com.example.study.demo.auth.repository.DatabaseTokenRepository;
import com.example.study.demo.auth.repository.DemoAccountRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * app 对 common 认证接口的具体实现。
 */
@Service
public class DemoTokenService implements TokenAuthenticator {

    private static final Duration TOKEN_TTL = Duration.ofHours(2);

    private final DemoAccountRepository accountRepository;
    private final DatabaseTokenRepository tokenRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordHasher passwordHasher;

    public DemoTokenService(DemoAccountRepository accountRepository,
                            DatabaseTokenRepository tokenRepository,
                            JwtTokenService jwtTokenService,
                            PasswordHasher passwordHasher) {
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordHasher = passwordHasher;
    }

    public LoginResponse login(LoginRequest request) {
        DemoAccount account = authenticateAccount(request);
        return LoginResponse.from(tokenRepository.create(account.toPrincipal(), TOKEN_TTL));
    }

    public LoginResponse loginWithJwt(LoginRequest request) {
        DemoAccount account = authenticateAccount(request);
        JwtToken jwtToken = jwtTokenService.create(account.toPrincipal());
        return LoginResponse.bearer(jwtToken.getToken(), jwtToken.getExpiresAt(), jwtToken.getPrincipal());
    }

    /**
     * AuthFilter 会调用这个方法，把请求中的 token 校验成 AuthPrincipal。
     */
    @Override
    public AuthPrincipal authenticate(String token) {
        if (jwtTokenService.supports(token)) {
            // 看起来像 JWT 的 token 不再回退 opaque token，避免验签失败后又被当成数据库 token 造成歧义。
            return jwtTokenService.authenticate(token);
        }
        return tokenRepository.findByToken(token)
                .map(StoredToken::getPrincipal)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED, "token无效或已过期"));
    }

    public void logout(String token) {
        tokenRepository.remove(token);
    }

    private DemoAccount authenticateAccount(LoginRequest request) {
        validateLoginRequest(request);
        String username = request.getUsername().trim();
        String password = request.getPassword();

        return accountRepository.findByUsername(username)
                .filter(candidate -> passwordHasher.matches(password, candidate.getPasswordHash()))
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED, "用户名或密码错误"));
    }

    private static void validateLoginRequest(LoginRequest request) {
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");
        AssertUtils.hasText(request.getUsername(), CommonErrorCode.PARAM_ERROR, "username不能为空");
        AssertUtils.hasText(request.getPassword(), CommonErrorCode.PARAM_ERROR, "password不能为空");
    }
}

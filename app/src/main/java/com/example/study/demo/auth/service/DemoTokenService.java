package com.example.study.demo.auth.service;

import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.TokenAuthenticator;
import com.example.study.demo.auth.domain.DemoAccount;
import com.example.study.demo.auth.domain.StoredToken;
import com.example.study.demo.auth.dto.LoginRequest;
import com.example.study.demo.auth.dto.LoginResponse;
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

    public DemoTokenService(DemoAccountRepository accountRepository, DatabaseTokenRepository tokenRepository) {
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername().trim();
        String password = request.getPassword();

        DemoAccount account = accountRepository.findByUsername(username)
                .filter(candidate -> candidate.matchesPassword(password))
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED, "用户名或密码错误"));

        return LoginResponse.from(tokenRepository.create(account.toPrincipal(), TOKEN_TTL));
    }

    /**
     * AuthFilter 会调用这个方法，把请求中的 token 校验成 AuthPrincipal。
     */
    @Override
    public AuthPrincipal authenticate(String token) {
        return tokenRepository.findByToken(token)
                .map(StoredToken::getPrincipal)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED, "token无效或已过期"));
    }

    public void logout(String token) {
        tokenRepository.remove(token);
    }
}

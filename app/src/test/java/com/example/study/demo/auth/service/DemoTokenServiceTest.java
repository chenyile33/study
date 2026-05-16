package com.example.study.demo.auth.service;

import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.study.demo.auth.domain.DemoAccount;
import com.example.study.demo.auth.domain.StoredToken;
import com.example.study.demo.auth.dto.LoginRequest;
import com.example.study.demo.auth.dto.LoginResponse;
import com.example.study.demo.auth.password.PasswordHasher;
import com.example.study.demo.auth.repository.DatabaseTokenRepository;
import com.example.study.demo.auth.repository.DemoAccountRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DemoTokenServiceTest {

    private static final Duration TOKEN_TTL = Duration.ofHours(2);

    private final DemoAccountRepository accountRepository = mock(DemoAccountRepository.class);
    private final DatabaseTokenRepository tokenRepository = mock(DatabaseTokenRepository.class);
    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
    private final DemoTokenService tokenService = new DemoTokenService(
            accountRepository,
            tokenRepository,
            passwordHasher
    );

    @Test
    void loginShouldRejectNullRequestBeforeRepository() {
        BusinessException exception = assertThrows(BusinessException.class, () -> tokenService.login(null));

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        verifyNoInteractions(accountRepository, tokenRepository, passwordHasher);
    }

    @Test
    void loginShouldRejectBlankUsernameBeforeRepository() {
        LoginRequest request = loginRequest(" ", "alice123");

        BusinessException exception = assertThrows(BusinessException.class, () -> tokenService.login(request));

        assertEquals(CommonErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        verifyNoInteractions(accountRepository, tokenRepository, passwordHasher);
    }

    @Test
    void loginShouldRejectWrongPasswordAndSkipTokenCreation() {
        LoginRequest request = loginRequest("alice", "wrong-password");
        DemoAccount account = demoAccount();
        when(accountRepository.findByUsername("alice")).thenReturn(Optional.of(account));
        when(passwordHasher.matches("wrong-password", account.getPasswordHash())).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> tokenService.login(request));

        assertEquals(AuthErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
        verify(tokenRepository, never()).create(any(AuthPrincipal.class), any(Duration.class));
    }

    @Test
    void loginShouldCreateTokenWhenPasswordMatches() {
        LoginRequest request = loginRequest(" alice ", "alice123");
        DemoAccount account = demoAccount();
        AuthPrincipal principal = account.toPrincipal();
        StoredToken storedToken = new StoredToken("token-value", principal, LocalDateTime.now().plus(TOKEN_TTL));
        when(accountRepository.findByUsername("alice")).thenReturn(Optional.of(account));
        when(passwordHasher.matches("alice123", account.getPasswordHash())).thenReturn(true);
        when(tokenRepository.create(any(AuthPrincipal.class), eq(TOKEN_TTL))).thenReturn(storedToken);

        LoginResponse response = tokenService.login(request);

        assertEquals("token-value", response.getAccessToken());
        assertEquals("2", response.getPrincipal().getPrincipalId());
        verify(accountRepository).findByUsername("alice");
        verify(tokenRepository).create(
                argThat(value -> value != null && "2".equals(value.getPrincipalId())),
                eq(TOKEN_TTL)
        );
    }

    private static LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private static DemoAccount demoAccount() {
        return new DemoAccount(
                "2",
                "alice",
                "password-hash",
                List.of("USER"),
                List.of("secure:read")
        );
    }
}

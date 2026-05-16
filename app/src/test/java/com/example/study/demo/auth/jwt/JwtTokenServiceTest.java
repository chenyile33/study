package com.example.study.demo.auth.jwt;

import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private static final Instant NOW = Instant.parse("2026-05-16T00:00:00Z");

    @Test
    void createAndAuthenticateShouldRestorePrincipalSnapshot() {
        JwtTokenService service = jwtTokenService(NOW);
        AuthPrincipal principal = AuthPrincipal.of(
                "2",
                "alice",
                List.of("USER"),
                List.of("secure:read"),
                Map.of("source", "test")
        );

        JwtToken jwtToken = service.create(principal, Duration.ofHours(2));
        AuthPrincipal actual = service.authenticate(jwtToken.getToken());

        assertTrue(service.supports(jwtToken.getToken()));
        assertEquals("2", actual.getPrincipalId());
        assertEquals("alice", actual.getPrincipalName());
        assertTrue(actual.hasRole("USER"));
        assertTrue(actual.hasPermission("secure:read"));
        assertEquals("test", actual.getAttribute("source").orElseThrow());
    }

    @Test
    void authenticateShouldRejectTamperedToken() {
        JwtTokenService service = jwtTokenService(NOW);
        JwtToken jwtToken = service.create(AuthPrincipal.of("2", "alice"), Duration.ofHours(2));
        String token = jwtToken.getToken();
        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("x") ? "y" : "x");

        AuthException exception = assertThrows(AuthException.class, () -> service.authenticate(tamperedToken));

        assertEquals(AuthErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
    }

    @Test
    void authenticateShouldRejectExpiredToken() {
        DemoJwtProperties properties = jwtProperties();
        JwtTokenService issuer = new JwtTokenService(new ObjectMapper(), properties, Clock.fixed(NOW, ZoneOffset.UTC));
        JwtTokenService verifier = new JwtTokenService(
                new ObjectMapper(),
                properties,
                Clock.fixed(NOW.plusSeconds(2), ZoneOffset.UTC)
        );
        JwtToken jwtToken = issuer.create(AuthPrincipal.of("2", "alice"), Duration.ofSeconds(1));

        AuthException exception = assertThrows(AuthException.class, () -> verifier.authenticate(jwtToken.getToken()));

        assertEquals(AuthErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
    }

    private static JwtTokenService jwtTokenService(Instant now) {
        return new JwtTokenService(
                new ObjectMapper(),
                jwtProperties(),
                Clock.fixed(now, ZoneOffset.UTC)
        );
    }

    private static DemoJwtProperties jwtProperties() {
        DemoJwtProperties properties = new DemoJwtProperties();
        properties.setSecret("unit-test-jwt-secret-change-me-at-least-32-bytes");
        properties.setTtlSeconds(7200);
        return properties;
    }
}

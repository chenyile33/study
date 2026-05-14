package com.example.common.core.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthContextTest {

    @AfterEach
    void clearContext() {
        AuthContext.clear();
    }

    @Test
    void requirePrincipalShouldThrowWhenNotAuthenticated() {
        AuthException exception = assertThrows(AuthException.class, AuthContext::requirePrincipal);

        assertEquals(AuthErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
        assertFalse(AuthContext.isAuthenticated());
    }

    @Test
    void openShouldExposePrincipalAndRestorePreviousPrincipalAfterClose() {
        AuthPrincipal alice = AuthPrincipal.of("1", "alice", List.of("USER"));
        AuthPrincipal admin = AuthPrincipal.of("2", "admin", List.of("ADMIN"));

        try (AuthScope ignored = AuthContext.open(alice)) {
            assertTrue(AuthContext.isAuthenticated());
            assertEquals("alice", AuthContext.requirePrincipal().getPrincipalName());

            try (AuthScope nested = AuthContext.open(admin)) {
                assertEquals("admin", AuthContext.requirePrincipal().getPrincipalName());
                assertTrue(AuthContext.hasRole("ADMIN"));
            }

            assertEquals("alice", AuthContext.requirePrincipal().getPrincipalName());
            assertTrue(AuthContext.hasRole("USER"));
        }

        assertFalse(AuthContext.isAuthenticated());
    }
}

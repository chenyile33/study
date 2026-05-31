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

    @Test
    void openAnonymousShouldClearPrincipalTemporarilyAndRestorePreviousPrincipal() {
        AuthPrincipal alice = AuthPrincipal.of("1", "alice", List.of("USER"));

        try (AuthScope ignored = AuthContext.open(alice)) {
            assertTrue(AuthContext.isAuthenticated());

            try (AuthScope anonymous = AuthContext.openAnonymous()) {
                assertFalse(AuthContext.isAuthenticated());
                assertThrows(AuthException.class, AuthContext::requirePrincipal);
            }

            assertEquals("alice", AuthContext.requirePrincipal().getPrincipalName());
        }

        assertFalse(AuthContext.isAuthenticated());
    }

    @Test
    void hasAnyRoleAndPermissionShouldReturnFalseForEmptyRequirements() {
        AuthPrincipal alice = AuthPrincipal.of(
                "1",
                "alice",
                List.of("USER"),
                List.of("secure:read"),
                java.util.Map.of()
        );

        try (AuthScope ignored = AuthContext.open(alice)) {
            assertFalse(AuthContext.hasAnyRole(null));
            assertFalse(AuthContext.hasAnyRole(List.of()));
            assertFalse(AuthContext.hasAnyPermission(null));
            assertFalse(AuthContext.hasAnyPermission(List.of()));
        }
    }
}

package com.example.common.core.auth;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthPrincipalTest {

    @Test
    void shouldNormalizeRolesPermissionsAndAttributes() {
        AuthPrincipal principal = AuthPrincipal.of(
                " 42 ",
                " alice ",
                List.of(" USER ", "", "ADMIN", "USER"),
                List.of(" secure:read ", "blog:create", " "),
                Map.of(" tenantId ", " t1 ")
        );

        assertEquals("42", principal.getPrincipalId());
        assertEquals("alice", principal.getPrincipalName());
        assertEquals(List.of("USER", "ADMIN"), List.copyOf(principal.getRoles()));
        assertEquals(List.of("secure:read", "blog:create"), List.copyOf(principal.getPermissions()));
        assertEquals(" t1 ", principal.getAttribute("tenantId").orElseThrow());
        assertTrue(principal.hasRole("USER"));
        assertTrue(principal.hasPermission(" secure:read "));
        assertFalse(principal.hasRole("UNKNOWN"));
    }

    @Test
    void shouldRejectBlankPrincipalId() {
        assertThrows(IllegalArgumentException.class, () -> AuthPrincipal.of(" ", "alice"));
    }
}

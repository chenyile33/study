package com.example.common.core.auth.authorization;

import com.example.common.core.auth.AuthPrincipal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultAuthorizationCheckerTest {

    private final DefaultAuthorizationChecker checker = new DefaultAuthorizationChecker();

    private final AuthPrincipal principal = AuthPrincipal.of(
            "1",
            "admin",
            List.of("ADMIN", "USER"),
            List.of("secure:read", "blog:create"),
            Map.of()
    );

    @Test
    void hasRolesShouldSupportAnyMode() {
        assertTrue(checker.hasRoles(principal, List.of("UNKNOWN", "ADMIN"), AuthorizationMode.ANY));
        assertFalse(checker.hasRoles(principal, List.of("UNKNOWN", "AUDITOR"), AuthorizationMode.ANY));
    }

    @Test
    void hasRolesShouldSupportAllMode() {
        assertTrue(checker.hasRoles(principal, List.of("ADMIN", "USER"), AuthorizationMode.ALL));
        assertFalse(checker.hasRoles(principal, List.of("ADMIN", "AUDITOR"), AuthorizationMode.ALL));
    }

    @Test
    void hasPermissionsShouldUseAnyModeWhenModeIsNull() {
        assertTrue(checker.hasPermissions(principal, List.of("missing", "secure:read"), null));
        assertFalse(checker.hasPermissions(principal, List.of("missing"), null));
    }

    @Test
    void emptyRequirementsShouldPass() {
        assertTrue(checker.hasRoles(principal, List.of(), AuthorizationMode.ALL));
        assertTrue(checker.hasPermissions(principal, null, AuthorizationMode.ANY));
    }
}

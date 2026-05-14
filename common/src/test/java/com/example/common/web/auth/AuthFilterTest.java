package com.example.common.web.auth;

import com.example.common.core.auth.AuthContext;
import com.example.common.core.auth.AuthErrorCode;
import com.example.common.core.auth.AuthException;
import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.AuthScope;
import com.example.common.core.auth.TokenAuthenticator;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthFilterTest {

    @AfterEach
    void clearContext() {
        AuthContext.clear();
    }

    @Test
    void permitPathShouldPassWithoutPrincipalAndRestoreOuterContext() throws Exception {
        CommonAuthProperties properties = enabledProperties();
        properties.setPermitPaths(List.of("/api/auth/login"));
        AuthFilter filter = authFilter(properties, token -> AuthPrincipal.of("1", "admin"));
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean authenticatedInsideChain = new AtomicBoolean(true);
        AuthPrincipal outerPrincipal = AuthPrincipal.of("outer", "outer-user");

        try (AuthScope ignored = AuthContext.open(outerPrincipal)) {
            filter.doFilter(request, response, (servletRequest, servletResponse) ->
                    authenticatedInsideChain.set(AuthContext.isAuthenticated()));

            assertEquals("outer-user", AuthContext.requirePrincipal().getPrincipalName());
        }

        assertEquals(200, response.getStatus());
        assertFalse(authenticatedInsideChain.get());
    }

    @Test
    void protectedPathWithoutTokenShouldReturnUnauthorizedAndSkipChain() throws Exception {
        AuthFilter filter = authFilter(enabledProperties(), token -> AuthPrincipal.of("1", "admin"));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/secure/ping");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, markCalled(chainCalled));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"code\":" + AuthErrorCode.UNAUTHORIZED.getCode()));
        assertFalse(chainCalled.get());
    }

    @Test
    void invalidTokenShouldReturnUnauthorizedAndSkipChain() throws Exception {
        AuthFilter filter = authFilter(enabledProperties(), token -> {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED, "bad token");
        });
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/secure/ping");
        request.addHeader("Authorization", "Bearer bad-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, markCalled(chainCalled));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("bad token"));
        assertFalse(chainCalled.get());
    }

    @Test
    void validTokenShouldExposePrincipalOnlyInsideFilterChain() throws Exception {
        AuthFilter filter = authFilter(enabledProperties(), token -> AuthPrincipal.of(
                "1",
                "admin",
                List.of("ADMIN"),
                List.of("secure:read"),
                Map.of()
        ));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/secure/ping");
        request.addHeader("Authorization", "Bearer good-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> principalNameInsideChain = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                principalNameInsideChain.set(AuthContext.requirePrincipal().getPrincipalName()));

        assertEquals(200, response.getStatus());
        assertEquals("admin", principalNameInsideChain.get());
        assertFalse(AuthContext.isAuthenticated());
    }

    private static CommonAuthProperties enabledProperties() {
        CommonAuthProperties properties = new CommonAuthProperties();
        properties.setEnabled(true);
        return properties;
    }

    private static AuthFilter authFilter(CommonAuthProperties properties, TokenAuthenticator tokenAuthenticator) {
        return new AuthFilter(properties, new BearerTokenResolver(properties), tokenAuthenticator);
    }

    private static FilterChain markCalled(AtomicBoolean chainCalled) {
        return (servletRequest, servletResponse) -> chainCalled.set(true);
    }
}

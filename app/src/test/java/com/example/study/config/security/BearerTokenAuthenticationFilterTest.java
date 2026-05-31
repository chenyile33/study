package com.example.study.config.security;

import com.example.common.core.auth.AuthPrincipal;
import com.example.common.core.auth.TokenAuthenticator;
import com.example.common.web.auth.BearerTokenResolver;
import com.example.common.web.auth.CommonAuthProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BearerTokenAuthenticationFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validTokenShouldExposePrincipalAndClearContextAfterRequest() throws Exception {
        AuthPrincipal principal = AuthPrincipal.of(
                "1",
                "admin",
                List.of("ADMIN"),
                List.of("secure:read"),
                Map.of()
        );
        BearerTokenAuthenticationFilter filter = bearerTokenFilter(token -> principal);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/security/secure/ping");
        request.addHeader("Authorization", "Bearer good-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Authentication> authenticationInsideChain = new AtomicReference<>();

        filter.doFilter(request, response, captureAuthentication(authenticationInsideChain));

        Authentication authentication = authenticationInsideChain.get();
        assertEquals("admin", ((AuthPrincipal) authentication.getPrincipal()).getPrincipalName());
        assertNull(authentication.getCredentials());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "secure:read".equals(authority.getAuthority())));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private static BearerTokenAuthenticationFilter bearerTokenFilter(TokenAuthenticator tokenAuthenticator) {
        CommonAuthProperties properties = new CommonAuthProperties();
        BearerTokenAuthenticationFilter filter = new BearerTokenAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "tokenResolver", new BearerTokenResolver(properties));
        ReflectionTestUtils.setField(filter, "tokenAuthenticator", tokenAuthenticator);
        return filter;
    }

    private static FilterChain captureAuthentication(AtomicReference<Authentication> authenticationInsideChain) {
        return (servletRequest, servletResponse) ->
                authenticationInsideChain.set(SecurityContextHolder.getContext().getAuthentication());
    }
}

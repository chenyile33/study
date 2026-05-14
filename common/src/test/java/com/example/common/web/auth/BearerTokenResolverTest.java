package com.example.common.web.auth;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BearerTokenResolverTest {

    @Test
    void resolveShouldReadBearerTokenCaseInsensitive() {
        CommonAuthProperties properties = new CommonAuthProperties();
        BearerTokenResolver resolver = new BearerTokenResolver(properties);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "bearer abc123");

        assertEquals("abc123", resolver.resolve(request).orElseThrow());
    }

    @Test
    void resolveShouldRejectMissingWhitespaceAfterPrefix() {
        CommonAuthProperties properties = new CommonAuthProperties();
        BearerTokenResolver resolver = new BearerTokenResolver(properties);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearerabc123");

        assertTrue(resolver.resolve(request).isEmpty());
    }

    @Test
    void resolveShouldUseWholeHeaderWhenPrefixIsBlank() {
        CommonAuthProperties properties = new CommonAuthProperties();
        properties.setTokenPrefix("");
        BearerTokenResolver resolver = new BearerTokenResolver(properties);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "raw-token");

        assertEquals("raw-token", resolver.resolve(request).orElseThrow());
    }
}

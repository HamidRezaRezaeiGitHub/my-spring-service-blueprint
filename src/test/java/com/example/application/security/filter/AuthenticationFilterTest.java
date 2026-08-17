package com.example.application.security.filter;

import com.example.application.authentication.AuthenticationService;
import com.example.application.security.SecurityExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class AuthenticationFilterTest {

    private final AuthenticationFilter filter = new AuthenticationFilter(
            mock(AuthenticationService.class), mock(SecurityExceptionHandler.class));

    @Test
    void shouldNotFilter_shouldAllowRegistrationTokenToReachRegistrationFlow() {
        // Arrange
        var request = new MockHttpServletRequest("POST", "/api/v1/auth/register");

        // Act and assert
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_shouldKeepAuthenticationEnabledForOtherPaths() {
        // Arrange
        var request = new MockHttpServletRequest("GET", "/api/v1/accounts/me");

        // Act and assert
        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_shouldRecognizeVersionedRegistrationUnderContextPath() {
        // Arrange
        var request = new MockHttpServletRequest("POST", "/service/api/v2/auth/register");
        request.setContextPath("/service");
        request.setServletPath("/api/v2/auth/register");

        // Act and assert
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_shouldAuthenticateUnsupportedRegistrationMethods() {
        // Arrange
        var request = new MockHttpServletRequest("GET", "/api/v1/auth/register");

        // Act and assert
        assertFalse(filter.shouldNotFilter(request));
    }
}

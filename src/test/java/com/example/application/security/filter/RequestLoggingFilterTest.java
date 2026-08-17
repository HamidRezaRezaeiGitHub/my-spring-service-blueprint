package com.example.application.security.filter;

import com.example.application.observability.CloudLoggingContextEnricher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestLoggingFilterTest {

    @Test
    void doFilterInternal_shouldNotReadSensitiveInputs_whenLoggingRequest() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        CloudLoggingContextEnricher contextEnricher = mock(CloudLoggingContextEnricher.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/accounts/me");
        when(response.getStatus()).thenReturn(200);

        // Act
        new RequestLoggingFilter(contextEnricher).doFilterInternal(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        verify(contextEnricher).applyTraceContext(request);
        verify(contextEnricher).clearTraceContext();
        verify(request, never()).getQueryString();
        verify(request, never()).getHeader("Authorization");
    }
}

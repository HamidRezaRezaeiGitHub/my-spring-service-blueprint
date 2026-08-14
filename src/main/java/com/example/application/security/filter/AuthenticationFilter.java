package com.example.application.security.filter;

import com.example.application.authentication.AuthenticationService;
import com.example.application.authentication.exception.TokenVerificationException;
import com.example.application.security.SecurityExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REGISTRATION_PATH = "/api/v1/auth/register";
    private final AuthenticationService authentication;
    private final SecurityExceptionHandler failures;

    public AuthenticationFilter(AuthenticationService authentication, SecurityExceptionHandler failures) {
        this.authentication = authentication;
        this.failures = failures;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return REGISTRATION_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            chain.doFilter(request, response);
            return;
        }
        if (!header.startsWith(BEARER_PREFIX) || header.length() == BEARER_PREFIX.length()) {
            failures.commence(request, response, new BadCredentialsException("Malformed Bearer authorization"));
            return;
        }
        try {
            var principal = authentication.authenticate(header.substring(BEARER_PREFIX.length()));
            var securityAuthentication = UsernamePasswordAuthenticationToken.authenticated(
                    principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(securityAuthentication);
            chain.doFilter(request, response);
        } catch (TokenVerificationException exception) {
            SecurityContextHolder.clearContext();
            failures.commence(request, response, new BadCredentialsException("Bearer token rejected", exception));
        }
    }
}

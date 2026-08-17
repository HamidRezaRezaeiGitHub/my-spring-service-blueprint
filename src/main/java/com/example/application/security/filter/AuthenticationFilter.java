package com.example.application.security.filter;

import com.example.application.authentication.AuthenticationService;
import com.example.application.authentication.TokenVerificationException;
import com.example.application.security.SecurityExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final RequestMatcher REGISTRATION = PathPatternRequestMatcher.pathPattern(
            HttpMethod.POST, "/api/v{version}/auth/register"
    );
    private final AuthenticationService authenticationService;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return REGISTRATION.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            chain.doFilter(request, response);
            return;
        }
        if (!header.startsWith(BEARER_PREFIX) || header.length() == BEARER_PREFIX.length()) {
            securityExceptionHandler.commence(request, response, new BadCredentialsException("Malformed Bearer authorization"));
            return;
        }
        try {
            var principal = authenticationService.authenticate(header.substring(BEARER_PREFIX.length()));
            var securityAuthentication = UsernamePasswordAuthenticationToken.authenticated(
                    principal, null, principal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(securityAuthentication);
            chain.doFilter(request, response);
        } catch (TokenVerificationException exception) {
            SecurityContextHolder.clearContext();
            securityExceptionHandler.commence(request, response, new BadCredentialsException("Bearer token rejected", exception));
        }
    }
}

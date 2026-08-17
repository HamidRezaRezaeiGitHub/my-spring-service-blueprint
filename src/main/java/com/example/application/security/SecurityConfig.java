package com.example.application.security;

import com.example.application.security.filter.AuthenticationFilter;
import com.example.application.security.filter.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationFilter authenticationFilter,
            RequestLoggingFilter loggingFilter,
            SecurityExceptionHandler exceptionHandler
    ) {
        // Bearer tokens are sent explicitly and no browser session cookie is used, so CSRF protection is unnecessary.
        http.csrf(AbstractHttpConfigurer::disable);

        // This JSON API never redirects clients to an HTML login form.
        http.formLogin(AbstractHttpConfigurer::disable);

        // Authentication is provider-backed Bearer authentication, not HTTP Basic credentials.
        http.httpBasic(AbstractHttpConfigurer::disable);

        // There is no server-side login state to terminate through Spring Security logout.
        http.logout(AbstractHttpConfigurer::disable);

        // Do not save rejected API requests in an HTTP session for a later browser redirect.
        http.requestCache(AbstractHttpConfigurer::disable);

        // Re-authenticate every request and never persist a SecurityContext in an HTTP session.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Render authentication and authorization failures through the same RFC 9457 advice as MVC failures.
        http.exceptionHandling(errors -> errors
                .authenticationEntryPoint(exceptionHandler)
                .accessDeniedHandler(exceptionHandler)
        );

        // Keep health, onboarding, the sample endpoint, and non-production API documentation publicly reachable.
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/actuator/health", "/api/v1/hello", "/api/v1/auth/register",
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                .permitAll()
                .anyRequest().authenticated());

        // Resolve a local account principal before Spring's username/password authentication position.
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Enclose authentication in request logging so rejected requests are measured without logging credentials.
        http.addFilterBefore(loggingFilter, AuthenticationFilter.class);

        // Build the single stateless filter chain used by REST, actuator, OpenAPI, and optional MCP endpoints.
        return http.build();
    }
}

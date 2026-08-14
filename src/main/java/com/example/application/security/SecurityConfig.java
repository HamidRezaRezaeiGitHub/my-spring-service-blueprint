package com.example.application.security;

import com.example.application.security.filter.AuthenticationFilter;
import com.example.application.security.filter.RequestLoggingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationFilter authenticationFilter,
                                            RequestLoggingFilter loggingFilter,
                                            SecurityExceptionHandler exceptionHandler,
                                            @Value("${app.security.enabled:true}") boolean securityEnabled)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint(exceptionHandler)
                        .accessDeniedHandler(exceptionHandler));

        if (securityEnabled) {
            http.authorizeHttpRequests(requests -> requests
                    .requestMatchers("/actuator/health", "/api/v1/hello", "/api/v1/auth/register",
                            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .anyRequest().authenticated());
        } else {
            http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        }

        return http.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

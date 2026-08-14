package com.example.application.api;

import com.example.application.pagination.StrictPageableHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Registers the blueprint's strict wrapper around Spring's standard Pageable request resolver.
 */
@Configuration
public class PaginationWebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new StrictPageableHandlerMethodArgumentResolver());
    }
}

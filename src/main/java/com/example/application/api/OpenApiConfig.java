package com.example.application.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Spring Service Blueprint API")
                        .version("1.0")
                        .description("Reusable service-template API contracts"));

        Components components = openAPI.getComponents();
        if (components == null) {
            components = new Components();
            openAPI.setComponents(components);
        }
        components.addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return openAPI;
    }

    @Bean
    OpenApiCustomizer problemDetailSchemaCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }
            Schema<?> problem = openApi.getComponents().getSchemas().get("ProblemDetail");
            if (problem == null || problem.getProperties() == null) {
                return;
            }
            problem.description("RFC 9457 problem details returned for HTTP failures");
            describe(problem, "type", "URI identifying the problem type; defaults to about:blank");
            describe(problem, "title", "Short, human-readable summary of the problem type");
            describe(problem, "status", "HTTP status code generated for this occurrence");
            describe(problem, "detail", "Human-readable explanation specific to this occurrence");
            describe(problem, "instance", "URI identifying the request that produced this occurrence");
            problem.getProperties().remove("properties");
            problem.addProperty("errors", new ArraySchema()
                    .description("Validation messages; present only when request validation fails")
                    .items(new StringSchema())
            );
        };
    }

    private static void describe(Schema<?> problem, String propertyName, String description) {
        Schema<?> property = problem.getProperties().get(propertyName);
        if (property != null) {
            property.description(description);
        }
    }
}

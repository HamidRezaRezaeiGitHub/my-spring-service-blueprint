package com.example.application.api;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Spring Service Blueprint API")
                        .version("1.0")
                        .description("Reusable service-template API contracts. Protected operations use Bearer JWTs, "
                                + "versioned routes start with `/api/v1`, and errors use RFC 9457 Problem Details.")
                )
                .tags(apiTags());

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

        addProblemResponse(components, "BadRequest", HttpStatus.BAD_REQUEST, "The request is malformed or fails validation.");
        addProblemResponse(components, "Unauthorized", HttpStatus.UNAUTHORIZED, "Bearer authentication is missing or rejected.");
        addProblemResponse(components, "Forbidden", HttpStatus.FORBIDDEN, "The authenticated caller is not allowed to perform this operation.");
        addProblemResponse(components, "NotFound", HttpStatus.NOT_FOUND, "The requested resource does not exist or is not visible to the caller.");
        addProblemResponse(components, "Conflict", HttpStatus.CONFLICT, "The request conflicts with the resource's current state.");
        addProblemResponse(components, "UnsupportedMediaType", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "The request Content-Type is not supported.");
        addProblemResponse(components, "InternalServerError", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.");
        addProblemResponse(components, "ServiceUnavailable", HttpStatus.SERVICE_UNAVAILABLE, "A required optional provider is disabled or temporarily unavailable.");

        return openAPI;
    }

    @Bean
    OpenApiCustomizer apiReferenceCustomizer() {
        return openApi -> {
            openApi.setTags(apiTags());

            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            openApi.getComponents().addSchemas("ProblemDetail", problemDetailSchema());
        };
    }

    private static List<Tag> apiTags() {
        return List.of(
                new Tag().name("Hello").description("Public REST and MCP adapter-parity example"),
                new Tag().name("Authentication").description("External identity verification and local account registration"),
                new Tag().name("Accounts").description("Authenticated account profile and resource-level access"),
                new Tag().name("Storage").description("Owner-scoped signed upload and download lifecycle"),
                new Tag().name("AI").description("Optional outbound chat-model integration")
        );
    }

    private static Schema<?> problemDetailSchema() {
        ArraySchema errors = new ArraySchema();
        errors.description("Validation messages; present only when request validation fails");
        errors.items(new StringSchema());

        return new ObjectSchema()
                .description("RFC 9457 problem details returned for HTTP failures")
                .addProperty("type", new StringSchema().format("uri").description("URI identifying the problem type; defaults to about:blank"))
                .addProperty("title", new StringSchema().description("Short, human-readable summary of the problem type"))
                .addProperty("status", new IntegerSchema().format("int32").description("HTTP status code generated for this occurrence"))
                .addProperty("detail", new StringSchema().description("Human-readable explanation specific to this occurrence"))
                .addProperty("instance", new StringSchema().format("uri").description("URI identifying the request that produced this occurrence"))
                .addProperty("errors", errors);
    }

    private static void addProblemResponse(Components components, String name, HttpStatus status, String description) {
        Schema<?> problemSchema = new Schema<>().$ref("#/components/schemas/ProblemDetail");
        MediaType mediaType = new MediaType()
                .schema(problemSchema)
                .example(Map.of(
                        "type", "about:blank",
                        "title", status.getReasonPhrase(),
                        "status", status.value(),
                        "detail", description,
                        "instance", "/api/v1/resource"
                ));
        components.addResponses(name, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(APPLICATION_PROBLEM_JSON_VALUE, mediaType))
        );
    }
}

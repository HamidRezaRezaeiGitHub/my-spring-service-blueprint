package com.example.application.api;

import com.example.application.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OpenApiContractIntegrationTest {

    private static final Set<String> HTTP_METHODS = Set.of(
            "get", "post", "put", "patch", "delete", "options", "head", "trace"
    );
    private static final Set<String> PUBLISHED_TAGS = Set.of(
            "AI", "Accounts", "Authentication", "Hello", "Storage"
    );
    private static final Set<String> PUBLISHED_SCHEMAS = Set.of(
            "AccountResponse",
            "CreateUploadRequest",
            "DownloadUrlResponse",
            "GenerateRequest",
            "GenerateResponse",
            "HelloResponse",
            "ProblemDetail",
            "RegisterAccountRequest",
            "Role",
            "StoragePurpose",
            "UploadUrlResponse"
    );
    private static final Map<String, Set<String>> EXPECTED_RESPONSES = Map.ofEntries(
            Map.entry("GET /api/v1/hello", Set.of("200", "500")),
            Map.entry("GET /api/v1/accounts/me", Set.of("200", "401", "404", "500")),
            Map.entry("GET /api/v1/accounts/{accountId}", Set.of("200", "400", "401", "403", "404", "500")),
            Map.entry("POST /api/v1/auth/register", Set.of("201", "400", "401", "409", "415", "500")),
            Map.entry("POST /api/v1/ai/generate", Set.of("200", "400", "401", "415", "500", "503")),
            Map.entry("POST /api/v1/storage/uploads", Set.of("200", "400", "401", "415", "500", "503")),
            Map.entry("POST /api/v1/storage/files/{fileId}/complete",
                    Set.of("204", "400", "401", "404", "409", "500", "503")),
            Map.entry("GET /api/v1/storage/files/{fileId}/download",
                    Set.of("200", "400", "401", "404", "409", "500", "503"))
    );

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void apiDocs_shouldExposeEveryConcreteVersionedPathAndBearerScheme() throws Exception {
        // Arrange, act, and assert
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Spring Service Blueprint API"))
                .andExpect(jsonPath("$.info.version").value("1.0"))
                .andExpect(jsonPath("$.info.description").value("Reusable service-template API contracts"))
                .andExpect(jsonPath("$.paths.length()").value(8))
                .andExpect(jsonPath("$.paths['/api/v1/hello'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/ai/generate'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/accounts/me'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/accounts/{accountId}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/storage/uploads'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/storage/files/{fileId}/complete'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/storage/files/{fileId}/download'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/auth/register'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/auth/register'].post.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/ai/generate'].post.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/storage/uploads'].post.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/accounts/me'].get.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/hello'].get.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/api/v1/auth/register'].post.parameters").doesNotExist())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"));
    }

    @Test
    void apiDocs_shouldDescribeEveryOperationParameterMediaTypeAndErrorResponse() throws Exception {
        // Arrange
        JsonNode apiDocs = apiDocs();
        JsonNode paths = apiDocs.path("paths");

        // Act and assert
        Set<String> actualTags = StreamSupport.stream(apiDocs.path("tags").spliterator(), false)
                .map(tag -> tag.path("name").textValue())
                .collect(Collectors.toSet());
        assertEquals(PUBLISHED_TAGS, actualTags);
        for (JsonNode tag : apiDocs.path("tags")) {
            assertHasText(tag.path("description"), tag.path("name").textValue() + " tag description");
        }

        for (var pathEntry : paths.properties()) {
            for (var operationEntry : pathEntry.getValue().properties()) {
                if (!HTTP_METHODS.contains(operationEntry.getKey())) {
                    continue;
                }
                String location = operationEntry.getKey().toUpperCase() + " " + pathEntry.getKey();
                JsonNode operation = operationEntry.getValue();
                assertHasText(operation.path("summary"), location + " summary");
                assertHasText(operation.path("description"), location + " description");
                assertHasText(operation.path("tags").path(0), location + " tag");
                assertFalse(operation.path("tags").path(0).textValue().endsWith("-controller"),
                        () -> location + " must use a human-facing tag");

                for (JsonNode parameter : operation.path("parameters")) {
                    assertHasText(parameter.path("description"), location + " parameter description");
                }

                JsonNode requestBody = operation.path("requestBody");
                if (!requestBody.isMissingNode()) {
                    assertTrue(requestBody.path("content").has(APPLICATION_JSON_VALUE),
                            () -> location + " request body must document application/json");
                }

                Set<String> actualResponses = operation.path("responses").properties().stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
                assertEquals(EXPECTED_RESPONSES.get(location), actualResponses,
                        () -> location + " response status documentation is incomplete");
                for (var responseEntry : operation.path("responses").properties()) {
                    String responseLocation = location + " response " + responseEntry.getKey();
                    JsonNode response = responseEntry.getValue();
                    assertHasText(response.path("description"), responseLocation + " description");
                    if (Integer.parseInt(responseEntry.getKey()) >= 400) {
                        assertTrue(response.path("content").has(APPLICATION_PROBLEM_JSON_VALUE),
                                () -> responseLocation + " must document application/problem+json");
                    } else if (!"204".equals(responseEntry.getKey())) {
                        assertTrue(response.path("content").has(APPLICATION_JSON_VALUE),
                                () -> responseLocation + " must document application/json");
                    }
                }
            }
        }
    }

    @Test
    void apiDocs_shouldDescribeEveryPublishedSchemaAndProperty() throws Exception {
        // Arrange
        JsonNode schemas = apiDocs().path("components").path("schemas");
        Set<String> actualSchemas = schemas.properties().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Act and assert
        assertEquals(PUBLISHED_SCHEMAS, actualSchemas);
        for (var schemaEntry : schemas.properties()) {
            String schemaName = schemaEntry.getKey();
            JsonNode schema = schemaEntry.getValue();
            assertHasText(schema.path("description"), schemaName + " description");

            JsonNode properties = schema.path("properties");
            for (var propertyEntry : properties.properties()) {
                String propertyLocation = schemaName + "." + propertyEntry.getKey();
                JsonNode property = propertyEntry.getValue();
                assertHasText(property.path("description"), propertyLocation + " description");
                assertTrue(property.has("type") || property.has("$ref"),
                        () -> propertyLocation + " must declare a type or schema reference");
                if (!"ProblemDetail".equals(schemaName)) {
                    assertTrue(isRequired(schema, propertyEntry.getKey()),
                            () -> propertyLocation + " must be required under the package's null-marked contract");
                    assertFalse(property.path("example").isMissingNode(),
                            () -> propertyLocation + " must provide a useful example");
                }
            }
        }

        assertEquals(Set.of("MEMBER", "ADMIN"), textValues(schemas.path("Role").path("enum")));
        assertEquals(Set.of("ATTACHMENT", "AVATAR", "EXPORT"),
                textValues(schemas.path("StoragePurpose").path("enum")));
        assertEquals(120, schemas.path("RegisterAccountRequest").path("properties")
                .path("displayName").path("maxLength").intValue());
        assertEquals(4_000, schemas.path("GenerateRequest").path("properties")
                .path("prompt").path("maxLength").intValue());
        assertEquals(255, schemas.path("CreateUploadRequest").path("properties")
                .path("filename").path("maxLength").intValue());
        assertEquals(128, schemas.path("CreateUploadRequest").path("properties")
                .path("contentType").path("maxLength").intValue());
        assertEquals(1, schemas.path("CreateUploadRequest").path("properties")
                .path("contentLength").path("minimum").intValue());
        assertFalse(schemas.has("Account"));
        assertFalse(schemas.has("StoredFile"));
        assertFalse(schemas.has("UserAuthentication"));
        assertFalse(schemas.has("VerifiedIdentity"));
    }

    private JsonNode apiDocs() throws Exception {
        String body = mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return jsonMapper.readTree(body);
    }

    private static void assertHasText(JsonNode node, String location) {
        assertTrue(node.isTextual() && !node.textValue().isBlank(), () -> location + " must be documented");
    }

    private static boolean isRequired(JsonNode schema, String propertyName) {
        for (JsonNode requiredProperty : schema.path("required")) {
            if (propertyName.equals(requiredProperty.textValue())) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> textValues(JsonNode array) {
        return StreamSupport.stream(array.spliterator(), false)
                .map(JsonNode::textValue)
                .collect(Collectors.toSet());
    }
}

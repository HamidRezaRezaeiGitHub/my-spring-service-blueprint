package com.example.application.ai;

import com.example.application.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.security.enabled=false")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AiControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void generate_shouldReturnServiceUnavailable_whenModelIsDisabled() throws Exception {
        // Arrange
        String request = """
                {"prompt":"Write a sentence"}
                """;

        // Act
        var result = mvc.perform(post("/api/v1/ai/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // Assert
        result.andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorType").value("SERVICE_UNAVAILABLE"));
    }

    @Test
    void generate_shouldReturnBadRequest_whenPromptIsTooLong() throws Exception {
        // Arrange
        String request = "{\"prompt\":\"" + "x".repeat(4001) + "\"}";

        // Act and assert
        mvc.perform(post("/api/v1/ai/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType").value("VALIDATION_ERROR"));
    }
}

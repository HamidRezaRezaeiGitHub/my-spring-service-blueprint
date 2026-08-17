package com.example.application.ai;

import com.example.application.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@WithMockUser
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
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.detail").value("AI generation is disabled"));
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
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0]").exists());
    }
}

package com.example.application.security;

import com.example.application.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(properties = {
        "spring.ai.mcp.server.enabled=true",
        "spring.ai.mcp.server.protocol=STATELESS"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class McpSecurityIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void mcp_shouldRequireAuthentication_whenServerIsEnabled() throws Exception {
        // Arrange
        String request = """
                {"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}
                """;

        // Act and assert
        mvc.perform(post("/mcp")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }
}

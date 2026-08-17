package com.example.application.hello;

import com.example.application.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.ai.mcp.server.enabled=true",
        "spring.ai.mcp.server.protocol=STATELESS"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@WithMockUser
class HelloMcpEndpointIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void toolsList_shouldDiscoverHello_whenStatelessMcpProfileIsEnabled() throws Exception {
        // Arrange
        String initialize = """
                {"jsonrpc":"2.0","id":0,"method":"initialize","params":{"protocolVersion":"2025-11-25","capabilities":{},"clientInfo":{"name":"integration-test","version":"1.0"}}}
                """;
        String request = """
                {"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}
                """;
        mvc.perform(post("/mcp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
                        .content(initialize))
                .andExpect(status().isOk());

        // Act
        var result = mvc.perform(post("/mcp")
                .header("MCP-Protocol-Version", "2025-11-25")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
                .content(request));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result.tools[?(@.name == 'hello')]").exists());
    }
}

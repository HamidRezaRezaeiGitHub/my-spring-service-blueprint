package com.example.application.api;

import com.example.application.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OpenApiContractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void apiDocs_shouldExposeConcreteVersionedPathsAndBearerScheme() throws Exception {
        // Arrange, act, and assert
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/hello'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/ai/generate'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/accounts/me'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/storage/uploads'].post").exists())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
    }
}

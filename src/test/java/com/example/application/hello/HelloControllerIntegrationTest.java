package com.example.application.hello;

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
class HelloControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void hello_shouldReturnGreeting_whenCalledWithoutCredentials() throws Exception {
        // Arrange and act
        var result = mvc.perform(get("/api/v1/hello"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from the Spring service blueprint!"));
    }
}

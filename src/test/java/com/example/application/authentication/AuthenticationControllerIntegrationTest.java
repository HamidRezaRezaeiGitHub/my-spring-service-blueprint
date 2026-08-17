package com.example.application.authentication;

import com.example.application.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void register_shouldReturnProblemDetail_whenAuthorizationHeaderIsMissing() throws Exception {
        // Arrange, act, and assert
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"Example Person\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("A required request value is missing or invalid"));
    }

    @Test
    void register_shouldReturnUnauthorizedProblemDetail_whenProviderRejectsToken() throws Exception {
        // Arrange, act, and assert
        mvc.perform(post("/api/v1/auth/register")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer opaque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"Example Person\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Bearer token was rejected"));
    }

    @Test
    void register_shouldReturnMethodNotAllowedProblemDetail_whenHttpMethodIsUnsupported() throws Exception {
        // Arrange, act, and assert
        mvc.perform(get("/api/v1/auth/register"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void register_shouldReturnUnsupportedMediaTypeProblemDetail_whenBodyIsNotJson() throws Exception {
        // Arrange, act, and assert
        mvc.perform(post("/api/v1/auth/register")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer opaque")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Example Person"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(415));
    }
}

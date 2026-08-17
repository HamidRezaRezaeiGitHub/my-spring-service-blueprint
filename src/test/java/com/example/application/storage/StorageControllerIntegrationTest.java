package com.example.application.storage;

import com.example.application.TestcontainersConfiguration;
import com.example.application.authorization.Role;
import com.example.application.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class StorageControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private StorageService storageService;

    @Test
    void completeUpload_shouldUseAuthenticatedOwner_whenRequestIsAuthorized() throws Exception {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        // Act and assert
        mvc.perform(post("/api/v1/storage/files/{fileId}/complete", fileId)
                        .with(authentication(authenticationFor(accountId))))
                .andExpect(status().isNoContent());
        verify(storageService).completeUpload(accountId, fileId);
    }

    @Test
    void completeUpload_shouldReturnNotFoundProblemDetail_whenOwnedFileIsUnavailable() throws Exception {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        doThrow(new StoredFileNotFoundException()).when(storageService).completeUpload(accountId, fileId);

        // Act and assert
        mvc.perform(post("/api/v1/storage/files/{fileId}/complete", fileId)
                        .with(authentication(authenticationFor(accountId))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    private static UsernamePasswordAuthenticationToken authenticationFor(UUID accountId) {
        CustomUserDetails principal = new CustomUserDetails(accountId, Role.MEMBER, null, List.of());
        return UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.authorities());
    }
}

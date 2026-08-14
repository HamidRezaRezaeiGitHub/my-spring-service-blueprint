package com.example.application.authentication.firebase;

import com.example.application.TestcontainersConfiguration;
import com.example.application.authentication.ExternalAuthenticationService;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

@SpringBootTest(properties = {
        "app.authentication.provider=firebase",
        "app.firebase.enabled=false"
})
@Import({TestcontainersConfiguration.class, FirebaseAuthenticationContextTest.MockFirebaseConfig.class})
class FirebaseAuthenticationContextTest {

    @Autowired
    private ExternalAuthenticationService authentication;

    @Test
    void context_shouldSelectFirebaseAdapter_whenProviderIsExplicitlyConfigured() {
        // Arrange and act are performed by Spring Boot context initialization.

        // Assert
        assertInstanceOf(FirebaseAuthenticationService.class, authentication);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MockFirebaseConfig {
        @Bean
        FirebaseAuth firebaseAuth() {
            return mock(FirebaseAuth.class);
        }
    }
}

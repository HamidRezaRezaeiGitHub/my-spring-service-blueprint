package com.example.application.authentication.noop;

import com.example.application.authentication.TokenVerificationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NoOpAuthenticationServiceTest {
    @Test
    void verify_shouldFailClosed_whenProviderIsDisabled() {
        // Arrange
        var service = new NoOpAuthenticationService();

        // Act and assert
        assertThrows(TokenVerificationException.class, () -> service.verify("anything"));
    }
}

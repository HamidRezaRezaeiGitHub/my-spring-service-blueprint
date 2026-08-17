package com.example.application;

import com.example.application.firebase.FirebaseProperties;
import com.example.application.storage.StorageProperties;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ConfigurationPropertiesValidationTest {

    @Test
    void storageProperties_shouldRejectInvalidProviderSizeAndDuration() {
        // Arrange
        StorageProperties properties = new StorageProperties();
        properties.setProvider("unknown");
        properties.setMaxFileSize(0);
        properties.setSignedUrlDuration(Duration.ofDays(8));

        // Act
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var violations = validatorFactory.getValidator().validate(properties);

            // Assert
            assertFalse(violations.isEmpty());
        }
    }

    @Test
    void firebaseProperties_shouldRejectNonPositiveTimeouts() {
        // Arrange
        FirebaseProperties properties = new FirebaseProperties();
        properties.setConnectTimeoutMs(0);
        properties.setReadTimeoutMs(-1);

        // Act
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var violations = validatorFactory.getValidator().validate(properties);

            // Assert
            assertFalse(violations.isEmpty());
        }
    }
}

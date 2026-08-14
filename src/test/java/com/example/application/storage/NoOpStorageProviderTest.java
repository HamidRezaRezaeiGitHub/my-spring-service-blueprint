package com.example.application.storage;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NoOpStorageProviderTest {
    private final NoOpStorageProvider provider = new NoOpStorageProvider();

    @Test
    void createUploadUrl_shouldFailClosed_whenStorageIsDisabled() {
        // Act and assert
        assertThrows(StorageUnavailableException.class,
                () -> provider.createUploadUrl("key", "text/plain", Duration.ofMinutes(1)));
    }

    @Test
    void exists_shouldFailClosed_whenStorageIsDisabled() {
        // Act and assert
        assertThrows(StorageUnavailableException.class, () -> provider.exists("key"));
    }
}

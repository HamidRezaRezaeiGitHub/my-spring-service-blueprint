package com.example.application.storage;

import com.example.application.storage.dto.CreateUploadRequest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StorageServiceTest {

    @Test
    void createUpload_shouldPersistGenericMetadata_whenRequestIsWithinLimit() {
        // Arrange
        StorageProvider provider = mock(StorageProvider.class);
        StoredFileRepository files = mock(StoredFileRepository.class);
        StorageProperties properties = properties(1024);
        when(files.save(any(StoredFile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(provider.createUploadUrl(anyString(), anyString(), any(Duration.class)))
                .thenReturn(URI.create("https://storage.invalid/signed"));
        var service = new StorageService(provider, properties, files);
        var request = new CreateUploadRequest(StoragePurpose.ATTACHMENT, "notes.txt", "text/plain", 512);

        // Act
        var result = service.createUpload(UUID.randomUUID(), request);

        // Assert
        assertEquals(URI.create("https://storage.invalid/signed"), result.uploadUrl());
    }

    @Test
    void createUpload_shouldRejectRequest_whenContentLengthExceedsLimit() {
        // Arrange
        var service = new StorageService(mock(StorageProvider.class), properties(10), mock(StoredFileRepository.class));
        var request = new CreateUploadRequest(StoragePurpose.ATTACHMENT, "large.bin", "application/octet-stream", 11);

        // Act and assert
        assertThrows(IllegalArgumentException.class, () -> service.createUpload(UUID.randomUUID(), request));
    }

    private static StorageProperties properties(long maximum) {
        StorageProperties properties = new StorageProperties();
        properties.setMaxFileSize(maximum);
        properties.setSignedUrlDuration(Duration.ofMinutes(5));
        return properties;
    }
}

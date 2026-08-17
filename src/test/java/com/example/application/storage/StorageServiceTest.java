package com.example.application.storage;

import com.example.application.storage.dto.CreateUploadRequest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        StoredFile savedFile = mock(StoredFile.class);
        when(savedFile.getId()).thenReturn(UUID.randomUUID());
        when(files.save(any(StoredFile.class))).thenReturn(savedFile);
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

    @Test
    void completeUpload_shouldEnableDownload_whenOwnedObjectExists() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        StoredFile file = new StoredFile(accountId, StoragePurpose.ATTACHMENT,
                "account/attachment/object.txt", "text/plain", 12);
        StorageProvider provider = mock(StorageProvider.class);
        StoredFileRepository files = mock(StoredFileRepository.class);
        when(files.findById(fileId)).thenReturn(java.util.Optional.of(file));
        when(provider.exists(file.getObjectKey())).thenReturn(true);
        var service = new StorageService(provider, properties(1024), files);

        // Act
        service.completeUpload(accountId, fileId);

        // Assert
        assertTrue(file.isUploaded());
    }

    @Test
    void completeUpload_shouldRejectFile_whenCallerDoesNotOwnIt() {
        // Arrange
        UUID fileId = UUID.randomUUID();
        StoredFile file = new StoredFile(UUID.randomUUID(), StoragePurpose.ATTACHMENT,
                "account/attachment/object.txt", "text/plain", 12);
        StoredFileRepository files = mock(StoredFileRepository.class);
        when(files.findById(fileId)).thenReturn(java.util.Optional.of(file));
        var service = new StorageService(mock(StorageProvider.class), properties(1024), files);

        // Act and assert
        assertThrows(StoredFileNotFoundException.class, () -> service.completeUpload(UUID.randomUUID(), fileId));
    }

    @Test
    void completeUpload_shouldRemainPending_whenProviderObjectDoesNotExist() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        StoredFile file = new StoredFile(accountId, StoragePurpose.ATTACHMENT,
                "account/attachment/object.txt", "text/plain", 12);
        StorageProvider provider = mock(StorageProvider.class);
        StoredFileRepository files = mock(StoredFileRepository.class);
        when(files.findById(fileId)).thenReturn(java.util.Optional.of(file));
        when(provider.exists(file.getObjectKey())).thenReturn(false);
        var service = new StorageService(provider, properties(1024), files);

        // Act and assert
        assertThrows(StoredObjectNotAvailableException.class, () -> service.completeUpload(accountId, fileId));
    }

    private static StorageProperties properties(long maximum) {
        StorageProperties properties = new StorageProperties();
        properties.setMaxFileSize(maximum);
        properties.setSignedUrlDuration(Duration.ofMinutes(5));
        return properties;
    }
}

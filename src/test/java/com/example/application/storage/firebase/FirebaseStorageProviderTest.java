package com.example.application.storage.firebase;

import com.example.application.storage.StorageProperties;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FirebaseStorageProviderTest {

    @Test
    void createUploadUrl_shouldReturnProviderSignedUrl_whenAdapterIsConfigured() throws Exception {
        // Arrange
        Storage storage = mock(Storage.class);
        StorageProperties properties = new StorageProperties();
        properties.setBucket("example-bucket");
        when(storage.signUrl(any(BlobInfo.class), anyLong(), eq(TimeUnit.SECONDS),
                any(Storage.SignUrlOption[].class)))
                .thenReturn(URI.create("https://storage.invalid/signed").toURL());
        var provider = new FirebaseStorageProvider(storage, properties);

        // Act
        URI result = provider.createUploadUrl("account/file.txt", "text/plain", Duration.ofMinutes(5));

        // Assert
        assertEquals(URI.create("https://storage.invalid/signed"), result);
    }
}

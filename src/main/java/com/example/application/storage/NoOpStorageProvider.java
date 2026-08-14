package com.example.application.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;

/** Credential-free default that fails every protected storage operation closed. */
@Component
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "noop", matchIfMissing = true)
public class NoOpStorageProvider implements StorageProvider {
    @Override
    public URI createUploadUrl(String objectKey, String contentType, Duration validity) {
        throw unavailable();
    }

    @Override
    public URI createDownloadUrl(String objectKey, Duration validity) {
        throw unavailable();
    }

    @Override
    public boolean exists(String objectKey) {
        throw unavailable();
    }

    private static StorageUnavailableException unavailable() {
        return new StorageUnavailableException("Object storage is disabled");
    }
}

package com.example.application.storage.firebase;

import com.example.application.storage.StorageProperties;
import com.example.application.storage.StorageProvider;
import com.example.application.storage.StorageUnavailableException;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "firebase")
public class FirebaseStorageProvider implements StorageProvider {
    private final Storage storage;
    private final String bucket;

    public FirebaseStorageProvider(Storage storage, StorageProperties properties) {
        this.storage = storage;
        this.bucket = properties.getBucket();
    }

    @Override
    public URI createUploadUrl(String objectKey, String contentType, Duration validity) {
        try {
            BlobInfo blob = BlobInfo.newBuilder(BlobId.of(bucket, objectKey)).setContentType(contentType).build();
            return storage.signUrl(blob, validity.toSeconds(), TimeUnit.SECONDS,
                    Storage.SignUrlOption.httpMethod(com.google.cloud.storage.HttpMethod.PUT),
                    Storage.SignUrlOption.withContentType(),
                    Storage.SignUrlOption.withV4Signature()).toURI();
        } catch (Exception exception) {
            throw new StorageUnavailableException("Unable to create signed upload URL", exception);
        }
    }

    @Override
    public URI createDownloadUrl(String objectKey, Duration validity) {
        try {
            BlobInfo blob = BlobInfo.newBuilder(BlobId.of(bucket, objectKey)).build();
            return storage.signUrl(blob, validity.toSeconds(), TimeUnit.SECONDS,
                    Storage.SignUrlOption.httpMethod(com.google.cloud.storage.HttpMethod.GET),
                    Storage.SignUrlOption.withV4Signature()).toURI();
        } catch (Exception exception) {
            throw new StorageUnavailableException("Unable to create signed download URL", exception);
        }
    }

    @Override
    public boolean exists(String objectKey) {
        return storage.get(BlobId.of(bucket, objectKey)) != null;
    }
}

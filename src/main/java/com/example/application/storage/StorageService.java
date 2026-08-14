package com.example.application.storage;

import com.example.application.storage.dto.CreateUploadRequest;
import com.example.application.storage.dto.DownloadUrlResponse;
import com.example.application.storage.dto.UploadUrlResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class StorageService {
    private final StorageProvider provider;
    private final StorageProperties properties;
    private final StoredFileRepository files;

    public StorageService(StorageProvider provider, StorageProperties properties, StoredFileRepository files) {
        this.provider = provider;
        this.properties = properties;
        this.files = files;
    }

    @Transactional
    public UploadUrlResponse createUpload(UUID accountId, CreateUploadRequest request) {
        if (request.contentLength() > properties.getMaxFileSize()) {
            throw new IllegalArgumentException("File exceeds configured maximum size");
        }
        String objectKey = objectKey(accountId, request.purpose(), request.filename());
        StoredFile file = files.save(new StoredFile(accountId, request.purpose(), objectKey,
                request.contentType(), request.contentLength()));
        var validity = properties.getSignedUrlDuration();
        var url = provider.createUploadUrl(objectKey, request.contentType(), validity);
        return new UploadUrlResponse(file.getId(), url, Instant.now().plus(validity));
    }

    @Transactional(readOnly = true)
    public DownloadUrlResponse createDownload(UUID accountId, UUID fileId) {
        StoredFile file = files.findById(fileId)
                .filter(candidate -> candidate.getOwnerAccountId().equals(accountId))
                .orElseThrow(() -> new IllegalArgumentException("Stored file was not found"));
        if (!file.isUploaded() || !provider.exists(file.getObjectKey())) {
            throw new IllegalArgumentException("Stored object is not available");
        }
        var validity = properties.getSignedUrlDuration();
        return new DownloadUrlResponse(provider.createDownloadUrl(file.getObjectKey(), validity),
                Instant.now().plus(validity));
    }

    private static String objectKey(UUID accountId, StoragePurpose purpose, String filename) {
        String safeName = filename.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "-")
                .replaceAll("-+", "-");
        return accountId + "/" + purpose.name().toLowerCase(Locale.ROOT) + "/" + UUID.randomUUID() + "-" + safeName;
    }
}

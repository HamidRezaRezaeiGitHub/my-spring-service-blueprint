package com.example.application.storage;

import com.example.application.storage.dto.CreateUploadRequest;
import com.example.application.storage.dto.DownloadUrlResponse;
import com.example.application.storage.dto.UploadUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageProvider provider;
    private final StorageProperties properties;
    private final StoredFileRepository repository;

    @Transactional
    public UploadUrlResponse createUpload(UUID accountId, CreateUploadRequest request) {
        if (request.contentLength() > properties.getMaxFileSize()) {
            throw new IllegalArgumentException("File exceeds configured maximum size");
        }
        String objectKey = objectKey(accountId, request.purpose(), request.filename());
        StoredFile file = repository.save(new StoredFile(accountId, request.purpose(), objectKey,
                request.contentType(), request.contentLength()));
        var validity = properties.getSignedUrlDuration();
        var url = provider.createUploadUrl(objectKey, request.contentType(), validity);
        return new UploadUrlResponse(
                requireNonNull(file.getId(), "Stored file must be persisted before creating its response"),
                url,
                Instant.now().plus(validity)
        );
    }

    @Transactional
    public void completeUpload(UUID accountId, UUID fileId) {
        StoredFile file = findOwnedFile(accountId, fileId);
        if (!provider.exists(file.getObjectKey())) {
            throw new StoredObjectNotAvailableException();
        }
        file.markUploaded();
    }

    @Transactional(readOnly = true)
    public DownloadUrlResponse createDownload(UUID accountId, UUID fileId) {
        StoredFile file = findOwnedFile(accountId, fileId);
        if (!file.isUploaded() || !provider.exists(file.getObjectKey())) {
            throw new StoredObjectNotAvailableException();
        }
        var validity = properties.getSignedUrlDuration();
        return new DownloadUrlResponse(provider.createDownloadUrl(file.getObjectKey(), validity),
                Instant.now().plus(validity));
    }

    private StoredFile findOwnedFile(UUID accountId, UUID fileId) {
        return repository.findById(fileId)
                .filter(candidate -> candidate.getOwnerAccountId().equals(accountId))
                .orElseThrow(StoredFileNotFoundException::new);
    }

    private static String objectKey(UUID accountId, StoragePurpose purpose, String filename) {
        String safeName = filename.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "-")
                .replaceAll("-+", "-");
        return accountId + "/" + purpose.name().toLowerCase(Locale.ROOT) + "/" + UUID.randomUUID() + "-" + safeName;
    }
}

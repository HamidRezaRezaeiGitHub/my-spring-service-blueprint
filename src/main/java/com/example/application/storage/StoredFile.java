package com.example.application.storage;

import com.example.application.persistence.UpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "stored_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredFile extends UpdatableEntity {
    @Id
    private UUID id;

    @Column(name = "owner_account_id", nullable = false)
    private UUID ownerAccountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private StoragePurpose purpose;

    @Column(name = "object_key", nullable = false, unique = true, length = 512)
    private String objectKey;

    @Column(name = "content_type", nullable = false, length = 128)
    private String contentType;

    @Column(name = "content_length", nullable = false)
    private long contentLength;

    @Column(nullable = false)
    private boolean uploaded;

    public StoredFile(UUID ownerAccountId, StoragePurpose purpose, String objectKey,
                      String contentType, long contentLength) {
        this.id = UUID.randomUUID();
        this.ownerAccountId = ownerAccountId;
        this.purpose = purpose;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public void markUploaded() {
        this.uploaded = true;
    }
}

package com.example.application.storage;

import java.net.URI;
import java.time.Duration;

/** Narrow signed-URL boundary implemented by optional object-storage adapters. */
public interface StorageProvider {
    URI createUploadUrl(String objectKey, String contentType, Duration validity);

    URI createDownloadUrl(String objectKey, Duration validity);

    boolean exists(String objectKey);
}

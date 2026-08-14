package com.example.application.storage;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app.storage")
public class StorageProperties {
    private String provider = "noop";
    private @Nullable String bucket;
    private long maxFileSize = 10 * 1024 * 1024;
    private Duration signedUrlDuration = Duration.ofMinutes(15);

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public @Nullable String getBucket() {
        return bucket;
    }

    public void setBucket(@Nullable String bucket) {
        this.bucket = bucket;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Duration getSignedUrlDuration() {
        return signedUrlDuration;
    }

    public void setSignedUrlDuration(Duration signedUrlDuration) {
        this.signedUrlDuration = signedUrlDuration;
    }
}

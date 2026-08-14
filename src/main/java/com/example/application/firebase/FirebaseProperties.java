package com.example.application.firebase;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("app.firebase")
public class FirebaseProperties {

    private boolean enabled;
    private @Nullable Resource serviceAccountKeyPath;
    private int connectTimeoutMs = 5_000;
    private int readTimeoutMs = 10_000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public @Nullable Resource getServiceAccountKeyPath() {
        return serviceAccountKeyPath;
    }

    public void setServiceAccountKeyPath(@Nullable Resource serviceAccountKeyPath) {
        this.serviceAccountKeyPath = serviceAccountKeyPath;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }
}

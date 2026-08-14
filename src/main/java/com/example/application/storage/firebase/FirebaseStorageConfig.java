package com.example.application.storage.firebase;

import com.example.application.storage.StorageProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "firebase")
public class FirebaseStorageConfig {

    @Bean
    Storage googleCloudStorage(GoogleCredentials credentials, StorageProperties properties) {
        if (properties.getBucket() == null || properties.getBucket().isBlank()) {
            throw new IllegalStateException("app.storage.bucket is required for the firebase storage provider");
        }
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}

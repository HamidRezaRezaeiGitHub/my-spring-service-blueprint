package com.example.application.storage.firebase;

import com.example.application.firebase.FirebaseProperties;
import com.example.application.storage.StorageProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "firebase")
public class FirebaseStorageConfig {

    @Bean
    Storage googleCloudStorage(GoogleCredentials credentials, StorageProperties storageProperties, FirebaseProperties firebaseProperties) {
        if (storageProperties.getBucket() == null || storageProperties.getBucket().isBlank()) {
            throw new IllegalStateException("app.storage.bucket is required for the firebase storage provider");
        }
        HttpTransportOptions transport = HttpTransportOptions.newBuilder()
                .setConnectTimeout(firebaseProperties.getConnectTimeoutMs())
                .setReadTimeout(firebaseProperties.getReadTimeoutMs())
                .build();
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setTransportOptions(transport)
                .build()
                .getService();
    }
}

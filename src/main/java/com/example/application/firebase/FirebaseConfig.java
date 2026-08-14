package com.example.application.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FirebaseProperties.class)
@ConditionalOnProperty(name = "app.firebase.enabled", havingValue = "true")
public class FirebaseConfig {

    @Bean
    GoogleCredentials firebaseCredentials(FirebaseProperties properties) throws IOException {
        Resource resource = properties.getServiceAccountKeyPath();
        if (resource == null || !resource.exists() || !resource.isReadable()) {
            throw new IllegalStateException("app.firebase.service-account-key-path must reference a readable credential file");
        }
        try (var input = resource.getInputStream()) {
            return GoogleCredentials.fromStream(input);
        }
    }

    @Bean(destroyMethod = "delete")
    FirebaseApp firebaseApp(GoogleCredentials credentials, FirebaseProperties properties) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setConnectTimeout(properties.getConnectTimeoutMs())
                .setReadTimeout(properties.getReadTimeoutMs())
                .build();
        return FirebaseApp.initializeApp(options, "application");
    }

    @Bean
    FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}

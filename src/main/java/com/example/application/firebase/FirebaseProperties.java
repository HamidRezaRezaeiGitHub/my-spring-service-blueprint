package com.example.application.firebase;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.firebase")
@Getter
@Setter
@Validated
public class FirebaseProperties {

    private boolean enabled;

    @Nullable
    private Resource serviceAccountKeyPath;

    @Min(1)
    @Max(120_000)
    private int connectTimeoutMs = 5_000;

    @Min(1)
    @Max(120_000)
    private int readTimeoutMs = 10_000;

}

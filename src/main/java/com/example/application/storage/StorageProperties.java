package com.example.application.storage;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("app.storage")
@Getter
@Setter
@Validated
public class StorageProperties {

    @NotBlank
    @Pattern(regexp = "noop|firebase", message = "must be either noop or firebase")
    private String provider = "noop";

    private @Nullable String bucket;

    @Positive
    private long maxFileSize = 10 * 1024 * 1024;

    @NotNull
    private Duration signedUrlDuration = Duration.ofMinutes(15);

    @AssertTrue(message = "signed URL duration must be between 1 second and 7 days")
    public boolean isSignedUrlDurationValid() {
        return !signedUrlDuration.isNegative() && !signedUrlDuration.isZero() && signedUrlDuration.compareTo(Duration.ofDays(7)) <= 0;
    }
}

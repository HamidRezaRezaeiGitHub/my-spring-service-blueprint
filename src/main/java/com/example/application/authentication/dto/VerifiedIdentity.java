package com.example.application.authentication.dto;

import org.jspecify.annotations.Nullable;

/** Provider-neutral identity produced only after external token verification succeeds. */
public record VerifiedIdentity(String subject, @Nullable String email, @Nullable String displayName) {
    public VerifiedIdentity {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Verified subject must not be blank");
        }
    }
}

package com.example.application.authentication.dto;

import org.jspecify.annotations.Nullable;

/**
 * Provider-neutral identity produced only after external token verification succeeds.
 *
 * @param subject stable provider-scoped identifier; this is the OIDC {@code sub} claim and the
 *                Firebase UID, and is intentionally used instead of changeable email as the lookup key
 * @param email   optional provider email claim; providers and tokens are not required to supply one
 */
public record VerifiedIdentity(
        String subject,
        @Nullable String email
) {
    public VerifiedIdentity {
        if (subject.isBlank()) {
            throw new IllegalArgumentException("Verified subject must not be blank");
        }
    }
}

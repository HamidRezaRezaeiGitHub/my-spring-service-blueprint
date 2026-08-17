package com.example.application.account.dto;

import com.example.application.account.Account;
import com.example.application.authorization.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Schema(description = "Public account representation")
public record AccountResponse(
        @Schema(description = "Persistent account identifier") UUID id,
        @Schema(description = "Application-facing account name") String displayName,
        @Schema(description = "Coarse authorization role") Role role,
        @Schema(description = "Whether authentication is allowed") boolean active,
        @Schema(description = "Creation instant", format = "date-time") Instant createdAt,
        @Schema(description = "Most recent update instant", format = "date-time") Instant lastUpdatedAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                requireNonNull(account.getId(), "Account must be persisted before conversion"),
                account.getDisplayName(),
                account.getRole(),
                account.isActive(),
                requireNonNull(account.getCreatedAt(), "Account must be persisted before conversion"),
                requireNonNull(account.getLastUpdatedAt(), "Account must be persisted before conversion")
        );
    }
}

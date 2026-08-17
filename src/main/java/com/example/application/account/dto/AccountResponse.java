package com.example.application.account.dto;

import com.example.application.account.Account;
import com.example.application.authorization.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Schema(description = "Public account representation")
public record AccountResponse(
        @Schema(description = "Persistent account identifier", example = "7f4ec68c-05b7-4a4c-a52c-7cfb30c73de7",
                requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(description = "Application-facing account name", example = "Example Person",
                requiredMode = Schema.RequiredMode.REQUIRED) String displayName,
        @Schema(description = "Coarse authorization role", example = "MEMBER",
                requiredMode = Schema.RequiredMode.REQUIRED) Role role,
        @Schema(description = "Whether authentication is allowed", example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED) boolean active,
        @Schema(description = "Creation instant", format = "date-time", example = "2026-08-17T14:30:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
        @Schema(description = "Most recent update instant", format = "date-time",
                example = "2026-08-17T15:45:00Z", requiredMode = Schema.RequiredMode.REQUIRED) Instant lastUpdatedAt
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

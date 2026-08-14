package com.example.application.account.dto;

import com.example.application.account.Account;
import com.example.application.authorization.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Public account representation")
public record AccountResponse(UUID id, String displayName, Role role, boolean active,
                              Instant createdAt, Instant lastUpdatedAt) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getDisplayName(), account.getRole(), account.isActive(),
                account.getCreatedAt(), account.getLastUpdatedAt());
    }
}

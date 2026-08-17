package com.example.application.account;

import com.example.application.authorization.Role;
import com.example.application.entity.AuditableEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Internal account model; controllers expose explicit response records")
public class Account extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Nullable
    @Schema(description = "Persistent account identifier", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Column(nullable = false, length = 120)
    @Schema(description = "Application-facing name; target projects can replace it with richer profile fields", example = "Example Person")
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Schema(description = "Coarse authorization role", accessMode = Schema.AccessMode.READ_ONLY)
    private Role role;

    @Column(nullable = false)
    @Schema(description = "Whether authentication is allowed for this account", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean active;

    public Account(String displayName, Role role) {
        this.displayName = requireDisplayName(displayName);
        this.role = requireNonNull(role, "Role must not be null");
        this.active = true;
    }

    public void rename(String displayName) {
        this.displayName = requireDisplayName(displayName);
    }

    private static String requireDisplayName(@Nullable String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name must not be blank");
        }
        return displayName.trim();
    }
}

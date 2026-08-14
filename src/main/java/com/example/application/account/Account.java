package com.example.application.account;

import com.example.application.authorization.Role;
import com.example.application.persistence.UpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends UpdatableEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    @Column(nullable = false)
    private boolean active;

    public Account(String displayName, Role role) {
        this.id = UUID.randomUUID();
        this.displayName = requireDisplayName(displayName);
        this.role = role;
        this.active = true;
    }

    public void rename(String displayName) {
        this.displayName = requireDisplayName(displayName);
    }

    private static String requireDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name must not be blank");
        }
        return displayName.trim();
    }
}

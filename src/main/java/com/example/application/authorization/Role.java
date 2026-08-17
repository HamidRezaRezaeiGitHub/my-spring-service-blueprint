package com.example.application.authorization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Coarse account roles. Features remain responsible for resource-level authorization.
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "Coarse account authorization role", enumAsRef = true)
public enum Role {

    MEMBER(Set.of("ACCOUNT_READ_SELF")),
    ADMIN(Set.of("ACCOUNT_READ_SELF", "ACCOUNT_READ_ANY", "ACCOUNT_MANAGE"));

    private final Set<String> authorities;

}

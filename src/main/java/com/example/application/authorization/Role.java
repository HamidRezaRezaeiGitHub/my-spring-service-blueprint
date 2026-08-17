package com.example.application.authorization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Coarse account roles. Features remain responsible for resource-level authorization.
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    MEMBER(Set.of("ACCOUNT_READ_SELF")),
    ADMIN(Set.of("ACCOUNT_READ_SELF", "ACCOUNT_READ_ANY", "ACCOUNT_MANAGE"));

    private final Set<String> authorities;

}

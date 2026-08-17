package com.example.application.account;

import com.example.application.authorization.Role;
import com.example.application.security.CustomUserDetails;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountAuthorizationTest {

    private final AccountAuthorization accountAuthorization = new AccountAuthorization();

    @Test
    void canRead_shouldAllowMember_whenAccountIsOwn() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        CustomUserDetails principal = new CustomUserDetails(accountId, Role.MEMBER, null, List.of());

        // Act and assert
        assertTrue(accountAuthorization.canRead(principal, accountId));
    }

    @Test
    void canRead_shouldRejectMember_whenAccountBelongsToAnotherMember() {
        // Arrange
        CustomUserDetails principal = new CustomUserDetails(UUID.randomUUID(), Role.MEMBER, null, List.of());

        // Act and assert
        assertFalse(accountAuthorization.canRead(principal, UUID.randomUUID()));
    }

    @Test
    void canRead_shouldAllowAdministrator_whenAccountBelongsToAnotherMember() {
        // Arrange
        CustomUserDetails principal = new CustomUserDetails(UUID.randomUUID(), Role.ADMIN, null, List.of());

        // Act and assert
        assertTrue(accountAuthorization.canRead(principal, UUID.randomUUID()));
    }
}

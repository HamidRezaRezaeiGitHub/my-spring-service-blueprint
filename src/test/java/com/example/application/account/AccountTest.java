package com.example.application.account;

import com.example.application.authorization.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AccountTest {

    @Test
    void equals_shouldUseObjectIdentity_whenTransientAccountsShareAuditState() {
        // Arrange
        Account first = new Account("First", Role.MEMBER);
        Account second = new Account("Second", Role.MEMBER);

        // Act and assert
        assertNotEquals(first, second);
    }
}

package com.example.application.account;

import com.example.application.TestcontainersConfiguration;
import com.example.application.authorization.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class AccountRepositoryIntegrationTest {

    private final AccountRepository accounts;

    @Autowired
    AccountRepositoryIntegrationTest(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Test
    void save_shouldPopulateAuditTimestamps_whenAccountIsPersisted() {
        // Arrange
        Account account = new Account("Example Person", Role.MEMBER);

        // Act
        Account saved = accounts.saveAndFlush(account);

        // Assert
        assertEquals(account.getId(), saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getLastUpdatedAt());
    }
}

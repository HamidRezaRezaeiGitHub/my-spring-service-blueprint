package com.example.application;

import com.example.application.account.Account;
import com.example.application.account.AccountRepository;
import com.example.application.authentication.UserAuthentication;
import com.example.application.authentication.UserAuthenticationRepository;
import com.example.application.authorization.Role;
import com.example.application.storage.StoragePurpose;
import com.example.application.storage.StoredFile;
import com.example.application.storage.StoredFileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class EntityIdentifierIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserAuthenticationRepository authenticationRepository;

    @Autowired
    private StoredFileRepository storedFileRepository;

    @Test
    void save_shouldGenerateUuidForEachConcreteEntity() {
        // Arrange
        Account account = accountRepository.saveAndFlush(new Account("Example Person", Role.MEMBER));
        UserAuthentication authentication = new UserAuthentication(
                "firebase", "provider-subject", null, account
        );
        StoredFile storedFile = new StoredFile(
                account.getId(), StoragePurpose.ATTACHMENT, "account/attachment/object.txt", "text/plain", 12
        );

        // Act
        authenticationRepository.saveAndFlush(authentication);
        storedFileRepository.saveAndFlush(storedFile);

        // Assert
        assertNotNull(account.getId());
        assertNotNull(authentication.getId());
        assertNotNull(storedFile.getId());
    }
}

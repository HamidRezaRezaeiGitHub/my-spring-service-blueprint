package com.example.application.authentication;

import com.example.application.account.Account;
import com.example.application.account.AccountService;
import com.example.application.authentication.dto.VerifiedIdentity;
import com.example.application.authentication.exception.TokenVerificationException;
import com.example.application.authorization.Role;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    @Test
    void authenticate_shouldMapVerifiedSubjectToLocalAccount_whenIdentityIsRegistered() {
        // Arrange
        ExternalAuthenticationService external = mock(ExternalAuthenticationService.class);
        UserAuthenticationRepository identities = mock(UserAuthenticationRepository.class);
        Account account = new Account("Example Person", Role.MEMBER);
        UserAuthentication identity = new UserAuthentication("firebase", "subject", "person@example.com", account);
        when(external.verify("opaque")).thenReturn(new VerifiedIdentity("subject", "person@example.com", null));
        when(identities.findByProviderAndProviderSubject("firebase", "subject")).thenReturn(Optional.of(identity));
        var service = new AuthenticationService(external, identities, mock(AccountService.class), "firebase");

        // Act
        var principal = service.authenticate("opaque");

        // Assert
        assertEquals(account.getId(), principal.accountId());
        assertEquals(Role.MEMBER, principal.role());
    }

    @Test
    void authenticate_shouldRejectVerifiedIdentity_whenNoLocalAccountMappingExists() {
        // Arrange
        ExternalAuthenticationService external = mock(ExternalAuthenticationService.class);
        UserAuthenticationRepository identities = mock(UserAuthenticationRepository.class);
        when(external.verify("opaque")).thenReturn(new VerifiedIdentity("subject", null, null));
        when(identities.findByProviderAndProviderSubject("firebase", "subject")).thenReturn(Optional.empty());
        var service = new AuthenticationService(external, identities, mock(AccountService.class), "firebase");

        // Act and assert
        assertThrows(TokenVerificationException.class, () -> service.authenticate("opaque"));
    }
}

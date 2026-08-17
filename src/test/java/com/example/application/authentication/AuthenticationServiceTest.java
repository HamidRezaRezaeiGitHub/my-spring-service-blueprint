package com.example.application.authentication;

import com.example.application.account.Account;
import com.example.application.account.AccountService;
import com.example.application.authentication.dto.VerifiedIdentity;
import com.example.application.authorization.Role;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    @Test
    void authenticate_shouldMapVerifiedSubjectToLocalAccount_whenIdentityIsRegistered() {
        // Arrange
        ExternalAuthenticationService external = mock(ExternalAuthenticationService.class);
        UserAuthenticationRepository identities = mock(UserAuthenticationRepository.class);
        Account account = mock(Account.class);
        UUID accountId = UUID.randomUUID();
        when(account.getId()).thenReturn(accountId);
        when(account.getRole()).thenReturn(Role.MEMBER);
        when(account.isActive()).thenReturn(true);
        UserAuthentication identity = new UserAuthentication("firebase", "subject", "person@example.com", account);
        when(external.verify("opaque")).thenReturn(new VerifiedIdentity("subject", "person@example.com"));
        when(identities.findByProviderAndProviderSubject("firebase", "subject")).thenReturn(Optional.of(identity));
        var service = new AuthenticationService(external, identities, mock(AccountService.class), "firebase");

        // Act
        var principal = service.authenticate("opaque");

        // Assert
        assertEquals(accountId, principal.accountId());
        assertEquals(Role.MEMBER, principal.role());
    }

    @Test
    void authenticate_shouldRejectVerifiedIdentity_whenNoLocalAccountMappingExists() {
        // Arrange
        ExternalAuthenticationService external = mock(ExternalAuthenticationService.class);
        UserAuthenticationRepository identities = mock(UserAuthenticationRepository.class);
        when(external.verify("opaque")).thenReturn(new VerifiedIdentity("subject", null));
        when(identities.findByProviderAndProviderSubject("firebase", "subject")).thenReturn(Optional.empty());
        var service = new AuthenticationService(external, identities, mock(AccountService.class), "firebase");

        // Act and assert
        assertThrows(TokenVerificationException.class, () -> service.authenticate("opaque"));
    }

    @Test
    void authenticate_shouldRejectVerifiedIdentity_whenLocalAccountIsInactive() {
        // Arrange
        ExternalAuthenticationService externalAuthenticationService = mock(ExternalAuthenticationService.class);
        UserAuthenticationRepository authenticationRepository = mock(UserAuthenticationRepository.class);
        Account account = mock(Account.class);
        when(account.isActive()).thenReturn(false);
        when(externalAuthenticationService.verify("opaque")).thenReturn(new VerifiedIdentity("subject", null));
        when(authenticationRepository.findByProviderAndProviderSubject("firebase", "subject"))
                .thenReturn(Optional.of(new UserAuthentication("firebase", "subject", null, account)));
        var authenticationService = new AuthenticationService(
                externalAuthenticationService, authenticationRepository, mock(AccountService.class), "firebase"
        );

        // Act and assert
        assertThrows(TokenVerificationException.class, () -> authenticationService.authenticate("opaque"));
    }

    @Test
    void register_shouldCreateAccountMapping_whenVerifiedIdentityIsNew() {
        // Arrange
        ExternalAuthenticationService externalAuthenticationService = mock(ExternalAuthenticationService.class);
        UserAuthenticationRepository authenticationRepository = mock(UserAuthenticationRepository.class);
        AccountService accountService = mock(AccountService.class);
        Account account = persistedAccount();
        when(externalAuthenticationService.verify("opaque"))
                .thenReturn(new VerifiedIdentity("subject", "person@example.com"));
        when(authenticationRepository.existsByProviderAndProviderSubject("firebase", "subject")).thenReturn(false);
        when(accountService.create("Example Person")).thenReturn(account);
        var authenticationService = new AuthenticationService(
                externalAuthenticationService, authenticationRepository, accountService, "firebase"
        );

        // Act
        var result = authenticationService.register("opaque", "Example Person");

        // Assert
        assertEquals(account.getId(), result.id());
        ArgumentCaptor<UserAuthentication> identity = ArgumentCaptor.forClass(UserAuthentication.class);
        verify(authenticationRepository).save(identity.capture());
        assertEquals("subject", identity.getValue().getProviderSubject());
        assertEquals("person@example.com", identity.getValue().getEmail());
    }

    @Test
    void register_shouldRejectIdentity_whenProviderSubjectAlreadyExists() {
        // Arrange
        ExternalAuthenticationService externalAuthenticationService = mock(ExternalAuthenticationService.class);
        UserAuthenticationRepository authenticationRepository = mock(UserAuthenticationRepository.class);
        AccountService accountService = mock(AccountService.class);
        when(externalAuthenticationService.verify("opaque")).thenReturn(new VerifiedIdentity("subject", null));
        when(authenticationRepository.existsByProviderAndProviderSubject("firebase", "subject")).thenReturn(true);
        var authenticationService = new AuthenticationService(
                externalAuthenticationService, authenticationRepository, accountService, "firebase"
        );

        // Act and assert
        assertThrows(IdentityAlreadyRegisteredException.class,
                () -> authenticationService.register("opaque", "Example Person"));
        verify(accountService, never()).create("Example Person");
    }

    private static Account persistedAccount() {
        Account account = mock(Account.class);
        when(account.getId()).thenReturn(UUID.randomUUID());
        when(account.getDisplayName()).thenReturn("Example Person");
        when(account.getRole()).thenReturn(Role.MEMBER);
        when(account.isActive()).thenReturn(true);
        when(account.getCreatedAt()).thenReturn(Instant.parse("2026-08-17T12:00:00Z"));
        when(account.getLastUpdatedAt()).thenReturn(Instant.parse("2026-08-17T12:00:00Z"));
        return account;
    }
}

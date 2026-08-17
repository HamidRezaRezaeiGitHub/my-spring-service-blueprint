package com.example.application.authentication.firebase;

import com.example.application.authentication.TokenVerificationException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirebaseAuthenticationServiceTest {

    @Test
    void verify_shouldReturnProviderNeutralIdentity_whenFirebaseAcceptsToken() throws Exception {
        // Arrange
        FirebaseAuth firebase = mock(FirebaseAuth.class);
        FirebaseToken token = mock(FirebaseToken.class);
        when(firebase.verifyIdToken("opaque-token", true)).thenReturn(token);
        when(token.getUid()).thenReturn("external-subject");
        when(token.getEmail()).thenReturn("person@example.com");
        var service = new FirebaseAuthenticationService(firebase);

        // Act
        var result = service.verify("opaque-token");

        // Assert
        assertEquals("external-subject", result.subject());
        assertEquals("person@example.com", result.email());
        verify(firebase).verifyIdToken("opaque-token", true);
    }

    @Test
    void verify_shouldHideProviderDetails_whenFirebaseRejectsToken() throws Exception {
        // Arrange
        FirebaseAuth firebase = mock(FirebaseAuth.class);
        when(firebase.verifyIdToken("rejected", true)).thenThrow(mock(FirebaseAuthException.class));
        var service = new FirebaseAuthenticationService(firebase);

        // Act and assert
        assertThrows(TokenVerificationException.class, () -> service.verify("rejected"));
    }

    @Test
    void verify_shouldAllowMissingEmail_whenProviderTokenHasNoEmailClaim() throws Exception {
        // Arrange
        FirebaseAuth firebase = mock(FirebaseAuth.class);
        FirebaseToken token = mock(FirebaseToken.class);
        when(firebase.verifyIdToken("opaque-token", true)).thenReturn(token);
        when(token.getUid()).thenReturn("external-subject");
        when(token.getEmail()).thenReturn(null);
        var service = new FirebaseAuthenticationService(firebase);

        // Act
        var result = service.verify("opaque-token");

        // Assert
        assertEquals("external-subject", result.subject());
        assertNull(result.email());
    }
}

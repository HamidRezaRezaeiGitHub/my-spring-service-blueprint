package com.example.application.authentication.firebase;

import com.example.application.authentication.ExternalAuthenticationService;
import com.example.application.authentication.dto.VerifiedIdentity;
import com.example.application.authentication.exception.TokenVerificationException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.authentication.provider", havingValue = "firebase")
public class FirebaseAuthenticationService implements ExternalAuthenticationService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public VerifiedIdentity verify(String bearerToken) {
        try {
            FirebaseToken token = firebaseAuth.verifyIdToken(bearerToken, true);
            return new VerifiedIdentity(token.getUid(), token.getEmail(), token.getName());
        } catch (FirebaseAuthException exception) {
            throw new TokenVerificationException("Bearer token was rejected by the configured provider", exception);
        }
    }
}

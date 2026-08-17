package com.example.application.authentication.firebase;

import com.example.application.authentication.ExternalAuthenticationService;
import com.example.application.authentication.TokenVerificationException;
import com.example.application.authentication.dto.VerifiedIdentity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.authentication.provider", havingValue = "firebase")
@RequiredArgsConstructor
public class FirebaseAuthenticationService implements ExternalAuthenticationService {

    private final FirebaseAuth firebaseAuth;

    @Override
    public VerifiedIdentity verify(String bearerToken) {
        try {
            FirebaseToken token = firebaseAuth.verifyIdToken(bearerToken, true);
            return new VerifiedIdentity(token.getUid(), token.getEmail());
        } catch (FirebaseAuthException exception) {
            throw new TokenVerificationException("Bearer token was rejected by the configured provider", exception);
        }
    }
}

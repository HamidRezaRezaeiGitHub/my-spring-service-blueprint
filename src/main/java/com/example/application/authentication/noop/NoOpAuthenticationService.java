package com.example.application.authentication.noop;

import com.example.application.authentication.ExternalAuthenticationService;
import com.example.application.authentication.TokenVerificationException;
import com.example.application.authentication.dto.VerifiedIdentity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Credential-free default that deliberately authenticates nobody.
 */
@Service
@ConditionalOnProperty(name = "app.authentication.provider", havingValue = "noop", matchIfMissing = true)
public class NoOpAuthenticationService implements ExternalAuthenticationService {
    @Override
    public VerifiedIdentity verify(String bearerToken) {
        throw new TokenVerificationException("External authentication is disabled");
    }
}

package com.example.application.authentication;

import com.example.application.authentication.dto.VerifiedIdentity;

/** Boundary implemented by optional external authentication providers. */
public interface ExternalAuthenticationService {
    VerifiedIdentity verify(String bearerToken);
}

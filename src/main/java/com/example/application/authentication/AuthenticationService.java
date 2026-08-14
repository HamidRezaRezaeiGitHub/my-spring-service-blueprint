package com.example.application.authentication;

import com.example.application.account.Account;
import com.example.application.account.AccountService;
import com.example.application.account.dto.AccountResponse;
import com.example.application.authentication.dto.VerifiedIdentity;
import com.example.application.authentication.exception.TokenVerificationException;
import com.example.application.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final ExternalAuthenticationService externalAuthentication;
    private final UserAuthenticationRepository identities;
    private final AccountService accounts;
    private final String providerName;

    public AuthenticationService(ExternalAuthenticationService externalAuthentication,
                                 UserAuthenticationRepository identities,
                                 AccountService accounts,
                                 @Value("${app.authentication.provider:noop}") String providerName) {
        this.externalAuthentication = externalAuthentication;
        this.identities = identities;
        this.accounts = accounts;
        this.providerName = providerName;
    }

    @Transactional(readOnly = true)
    public CustomUserDetails authenticate(String bearerToken) {
        VerifiedIdentity verified = externalAuthentication.verify(bearerToken);
        UserAuthentication identity = identities
                .findByProviderAndProviderSubject(providerName, verified.subject())
                .orElseThrow(() -> new TokenVerificationException("Verified identity is not registered"));
        Account account = identity.getAccount();
        if (!account.isActive()) {
            throw new TokenVerificationException("Account is disabled");
        }
        return CustomUserDetails.from(account, identity.getEmail());
    }

    @Transactional
    public AccountResponse register(String bearerToken, String displayName) {
        VerifiedIdentity verified = externalAuthentication.verify(bearerToken);
        if (identities.existsByProviderAndProviderSubject(providerName, verified.subject())) {
            throw new IdentityAlreadyRegisteredException();
        }
        Account account = accounts.create(displayName);
        identities.save(new UserAuthentication(providerName, verified.subject(), verified.email(), account));
        return AccountResponse.from(account);
    }
}

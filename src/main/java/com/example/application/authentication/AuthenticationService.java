package com.example.application.authentication;

import com.example.application.account.Account;
import com.example.application.account.AccountService;
import com.example.application.account.dto.AccountResponse;
import com.example.application.authentication.dto.VerifiedIdentity;
import com.example.application.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final ExternalAuthenticationService externalAuthenticationService;
    private final UserAuthenticationRepository authenticationRepository;
    private final AccountService accountService;
    private final String providerName;

    public AuthenticationService(ExternalAuthenticationService externalAuthenticationService,
                                 UserAuthenticationRepository authenticationRepository,
                                 AccountService accountService,
                                 @Value("${app.authentication.provider:noop}") String providerName) {
        this.externalAuthenticationService = externalAuthenticationService;
        this.authenticationRepository = authenticationRepository;
        this.accountService = accountService;
        this.providerName = providerName;
    }

    @Transactional(readOnly = true)
    public CustomUserDetails authenticate(String bearerToken) {
        VerifiedIdentity verified = externalAuthenticationService.verify(bearerToken);
        UserAuthentication identity = authenticationRepository
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
        VerifiedIdentity verified = externalAuthenticationService.verify(bearerToken);
        if (authenticationRepository.existsByProviderAndProviderSubject(providerName, verified.subject())) {
            throw new IdentityAlreadyRegisteredException();
        }
        Account account = accountService.create(displayName);
        authenticationRepository.save(new UserAuthentication(providerName, verified.subject(), verified.email(), account));
        return AccountResponse.from(account);
    }
}

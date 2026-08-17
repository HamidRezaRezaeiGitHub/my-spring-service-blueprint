package com.example.application.account;

import com.example.application.authorization.Role;
import com.example.application.security.CustomUserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resource-level authorization owned by the account feature.
 */
@Component
public class AccountAuthorization {

    public boolean canRead(CustomUserDetails principal, UUID accountId) {
        return principal.accountId().equals(accountId) || principal.role() == Role.ADMIN;
    }
}

package com.example.application.security;

import com.example.application.account.Account;
import com.example.application.authorization.Role;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Authenticated local account principal; provider tokens are never retained.
 */
public record CustomUserDetails(
        UUID accountId,
        Role role,
        @Nullable String email,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public static CustomUserDetails from(Account account, @Nullable String email) {
        var authorities = Stream.concat(
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name())),
                        account.getRole().getAuthorities().stream().map(SimpleGrantedAuthority::new)
                )
                .toList();
        UUID id = account.getId();
        if (id == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        return new CustomUserDetails(id, account.getRole(), email, authorities);
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return accountId.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}

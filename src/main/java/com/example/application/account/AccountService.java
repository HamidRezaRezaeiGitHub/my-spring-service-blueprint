package com.example.application.account;

import com.example.application.account.dto.AccountResponse;
import com.example.application.authorization.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    @Transactional
    public Account create(String displayName) {
        return repository.save(new Account(displayName, Role.MEMBER));
    }

    @Transactional(readOnly = true)
    public Account get(UUID accountId) {
        return repository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional(readOnly = true)
    public AccountResponse getResponse(UUID accountId) {
        return AccountResponse.from(get(accountId));
    }
}

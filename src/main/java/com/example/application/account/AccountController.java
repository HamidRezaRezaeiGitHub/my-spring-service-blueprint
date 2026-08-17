package com.example.application.account;

import com.example.application.account.dto.AccountResponse;
import com.example.application.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.application.api.WebApiConfig.API_V1;

@RestController
@RequestMapping(path = "/api/v{version}/accounts", version = API_V1)
@Tag(name = "Accounts")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping("/me")
    @Operation(summary = "Return the authenticated account")
    public AccountResponse me(@AuthenticationPrincipal CustomUserDetails principal) {
        return service.getResponse(principal.accountId());
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("@accountAuthorization.canRead(principal, #accountId)")
    @Operation(summary = "Return an account when the caller owns it or is an administrator")
    public AccountResponse get(
            @SuppressWarnings("unused") @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID accountId
    ) {
        return service.getResponse(accountId);
    }
}

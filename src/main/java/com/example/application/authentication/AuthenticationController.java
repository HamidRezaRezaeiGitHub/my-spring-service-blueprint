package com.example.application.authentication;

import com.example.application.account.dto.AccountResponse;
import com.example.application.authentication.dto.RegisterAccountRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.application.api.WebApiConfig.API_V1;

@RestController
@RequestMapping(path = "/api/v{version}/auth", version = API_V1)
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    public AccountResponse register(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody RegisterAccountRequest request
    ) {
        if (!authorization.startsWith(BEARER_PREFIX) || authorization.length() == BEARER_PREFIX.length()) {
            throw new IllegalArgumentException("A Bearer token is required");
        }
        return service.register(authorization.substring(BEARER_PREFIX.length()), request.displayName());
    }
}

package com.example.application.authentication;

import com.example.application.account.dto.AccountResponse;
import com.example.application.authentication.dto.RegisterAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.example.application.api.WebApiConfig.API_V1;

@RestController
@RequestMapping(path = "/api/v{version}/auth", version = API_V1)
public class AuthenticationController {

    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationService authentication;

    public AuthenticationController(AuthenticationService authentication) {
        this.authentication = authentication;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                    @Valid @RequestBody RegisterAccountRequest request) {
        if (!authorization.startsWith(BEARER_PREFIX) || authorization.length() == BEARER_PREFIX.length()) {
            throw new IllegalArgumentException("A Bearer token is required");
        }
        return authentication.register(authorization.substring(BEARER_PREFIX.length()), request.displayName());
    }
}

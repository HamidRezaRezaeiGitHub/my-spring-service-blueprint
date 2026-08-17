package com.example.application.authentication;

import com.example.application.account.dto.AccountResponse;
import com.example.application.authentication.dto.RegisterAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.example.application.api.WebApiConfig.API_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/v{version}/auth", version = API_V1, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "External identity verification and local account registration")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationService service;

    @PostMapping(path = "/register", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Register an account",
            description = "Verifies the external Bearer identity and creates its local account mapping.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account registered",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "415", ref = "#/components/responses/UnsupportedMediaType"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public AccountResponse register(
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody RegisterAccountRequest request
    ) {
        if (!authorization.startsWith(BEARER_PREFIX) || authorization.length() == BEARER_PREFIX.length()) {
            throw new IllegalArgumentException("A Bearer token is required");
        }
        return service.register(authorization.substring(BEARER_PREFIX.length()), request.displayName());
    }
}

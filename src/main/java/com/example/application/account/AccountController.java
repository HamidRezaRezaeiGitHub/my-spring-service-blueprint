package com.example.application.account;

import com.example.application.account.dto.AccountResponse;
import com.example.application.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.application.api.WebApiConfig.API_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/v{version}/accounts", version = API_V1, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Accounts", description = "Authenticated account profile and resource-level access")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping("/me")
    @Operation(summary = "Get my account", description = "Returns the local account mapped to the verified Bearer identity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated account",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "Bearer authentication is missing or rejected",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "The mapped account no longer exists",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AccountResponse me(@AuthenticationPrincipal CustomUserDetails principal) {
        return service.getResponse(principal.accountId());
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("@accountAuthorization.canRead(principal, #accountId)")
    @Operation(summary = "Get an account",
            description = "Returns an account when the caller owns it or has administrative access.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requested account",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "The account identifier is malformed",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Bearer authentication is missing or rejected",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "The caller cannot read the requested account",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "The requested account does not exist",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public AccountResponse get(
            @Parameter(hidden = true) @SuppressWarnings("unused")
            @AuthenticationPrincipal CustomUserDetails principal,
            @Parameter(description = "Persistent account identifier", required = true,
                    example = "7f4ec68c-05b7-4a4c-a52c-7cfb30c73de7") @PathVariable UUID accountId
    ) {
        return service.getResponse(accountId);
    }
}

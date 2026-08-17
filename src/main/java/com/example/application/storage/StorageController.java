package com.example.application.storage;

import com.example.application.security.CustomUserDetails;
import com.example.application.storage.dto.CreateUploadRequest;
import com.example.application.storage.dto.DownloadUrlResponse;
import com.example.application.storage.dto.UploadUrlResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.application.api.WebApiConfig.API_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/v{version}/storage", version = API_V1, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Storage", description = "Owner-scoped signed upload and download lifecycle")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping(path = "/uploads", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create an upload URL",
            description = "Creates pending file metadata and a temporary provider URL for the authenticated owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pending file and signed upload URL",
                    content = @Content(schema = @Schema(implementation = UploadUrlResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "415", ref = "#/components/responses/UnsupportedMediaType"),
            @ApiResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public UploadUrlResponse createUpload(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails principal,
                                          @Valid @RequestBody CreateUploadRequest request) {
        return storageService.createUpload(principal.accountId(), request);
    }

    @PostMapping("/files/{fileId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Complete an upload",
            description = "Verifies that the authenticated owner's provider object exists before enabling downloads.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Upload verified and marked complete"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public void completeUpload(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails principal,
                               @Parameter(description = "Pending file identifier", required = true,
                                       example = "7f4ec68c-05b7-4a4c-a52c-7cfb30c73de7") @PathVariable UUID fileId) {
        storageService.completeUpload(principal.accountId(), fileId);
    }

    @GetMapping("/files/{fileId}/download")
    @Operation(summary = "Create a download URL",
            description = "Creates a temporary download URL for a verified object owned by the authenticated account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signed download URL",
                    content = @Content(schema = @Schema(implementation = DownloadUrlResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public DownloadUrlResponse download(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails principal,
                                        @Parameter(description = "Verified file identifier", required = true,
                                                example = "7f4ec68c-05b7-4a4c-a52c-7cfb30c73de7")
                                        @PathVariable UUID fileId) {
        return storageService.createDownload(principal.accountId(), fileId);
    }
}

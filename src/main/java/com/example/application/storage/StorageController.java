package com.example.application.storage;

import com.example.application.security.CustomUserDetails;
import com.example.application.storage.dto.CreateUploadRequest;
import com.example.application.storage.dto.DownloadUrlResponse;
import com.example.application.storage.dto.UploadUrlResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@RestController
@RequestMapping(path = "/api/v{version}/storage", version = API_V1)
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping("/uploads")
    public UploadUrlResponse createUpload(@AuthenticationPrincipal CustomUserDetails principal,
                                          @Valid @RequestBody CreateUploadRequest request) {
        return storageService.createUpload(principal.accountId(), request);
    }

    @PostMapping("/files/{fileId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeUpload(@AuthenticationPrincipal CustomUserDetails principal,
                               @PathVariable UUID fileId) {
        storageService.completeUpload(principal.accountId(), fileId);
    }

    @GetMapping("/files/{fileId}/download")
    public DownloadUrlResponse download(@AuthenticationPrincipal CustomUserDetails principal,
                                        @PathVariable UUID fileId) {
        return storageService.createDownload(principal.accountId(), fileId);
    }
}

package com.example.application.storage;

import com.example.application.security.CustomUserDetails;
import com.example.application.storage.dto.CreateUploadRequest;
import com.example.application.storage.dto.DownloadUrlResponse;
import com.example.application.storage.dto.UploadUrlResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.application.api.WebApiConfig.API_V1;

@RestController
@RequestMapping(path = "/api/v{version}/storage", version = API_V1)
public class StorageController {
    private final StorageService storage;

    public StorageController(StorageService storage) {
        this.storage = storage;
    }

    @PostMapping("/uploads")
    public UploadUrlResponse createUpload(@AuthenticationPrincipal CustomUserDetails principal,
                                          @Valid @RequestBody CreateUploadRequest request) {
        return storage.createUpload(principal.accountId(), request);
    }

    @GetMapping("/files/{fileId}/download")
    public DownloadUrlResponse download(@AuthenticationPrincipal CustomUserDetails principal,
                                        @PathVariable UUID fileId) {
        return storage.createDownload(principal.accountId(), fileId);
    }
}

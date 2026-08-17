package com.example.application.storage;

import io.swagger.v3.oas.annotations.media.Schema;

/** Generic namespaces; applications may extend this enum with feature-owned purposes. */
@Schema(description = "Generic business purpose used to namespace and govern a stored object", enumAsRef = true)
public enum StoragePurpose {
    ATTACHMENT,
    AVATAR,
    EXPORT
}

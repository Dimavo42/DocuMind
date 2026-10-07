package com.documind.dto.response;

import com.documind.enums.DocumentStatus;
import com.documind.enums.DocumentType;

import java.time.Instant;

public record DocumentResponse(
        Long id,
        String filename,
        DocumentType type,
        DocumentStatus status,
        long sizeBytes,
        int chunkCount,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {
}

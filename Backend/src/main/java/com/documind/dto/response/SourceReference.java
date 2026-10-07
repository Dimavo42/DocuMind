package com.documind.dto.response;

public record SourceReference(
        Long documentId,
        String documentName,
        int chunkIndex,
        double similarity,
        String excerpt
) {
}

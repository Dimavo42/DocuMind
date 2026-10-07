package com.documind.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "documind.rag")
public record RagProperties(
        @Min(100) int chunkSize,
        @Min(0) int chunkOverlap,
        @Min(1) int topK,
        @Min(1) int embeddingBatchSize
) {
    public RagProperties {
        if (chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException("documind.rag.chunk-overlap must be smaller than chunk-size");
        }
    }
}

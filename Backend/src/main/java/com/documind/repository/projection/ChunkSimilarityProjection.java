package com.documind.repository.projection;

/**
 * Read model for a single vector-search hit.
 */
public interface ChunkSimilarityProjection {

    Long getChunkId();

    Long getDocumentId();

    String getDocumentName();

    Integer getChunkIndex();

    String getContent();

    Double getSimilarity();
}

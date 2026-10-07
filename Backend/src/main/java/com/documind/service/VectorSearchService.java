package com.documind.service;

import com.documind.enums.DocumentStatus;
import com.documind.repository.DocumentChunkRepository;
import com.documind.repository.projection.ChunkSimilarityProjection;
import com.documind.util.VectorUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorSearchService {

    private final DocumentChunkRepository chunkRepository;

    public VectorSearchService(DocumentChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    /**
     * Finds the chunks of READY documents that are most similar to the query embedding.
     */
    public List<ChunkSimilarityProjection> findMostSimilarChunks(float[] queryEmbedding, int topK) {
        return chunkRepository.findMostSimilar(
                VectorUtils.toPgVectorLiteral(queryEmbedding),
                DocumentStatus.READY.name(),
                topK);
    }
}

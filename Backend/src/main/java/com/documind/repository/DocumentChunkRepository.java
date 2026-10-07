package com.documind.repository;

import com.documind.entity.DocumentChunk;
import com.documind.repository.projection.ChunkSimilarityProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    /**
     * Returns the chunks closest to the given embedding using cosine distance ({@code <=>}).
     * The embedding is passed in pgvector text format, e.g. {@code [0.1,0.2,...]}.
     */
    @Query(value = """
            SELECT c.id                                              AS "chunkId",
                   d.id                                              AS "documentId",
                   d.original_filename                               AS "documentName",
                   c.chunk_index                                     AS "chunkIndex",
                   c.content                                         AS "content",
                   1 - (c.embedding <=> CAST(:embedding AS vector))  AS "similarity"
            FROM document_chunks c
            JOIN documents d ON d.id = c.document_id
            WHERE d.status = :status
            ORDER BY c.embedding <=> CAST(:embedding AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<ChunkSimilarityProjection> findMostSimilar(@Param("embedding") String embedding,
                                                    @Param("status") String status,
                                                    @Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("DELETE FROM DocumentChunk c WHERE c.document.id = :documentId")
    void deleteByDocumentId(@Param("documentId") Long documentId);
}

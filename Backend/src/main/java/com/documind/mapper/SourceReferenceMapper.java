package com.documind.mapper;

import com.documind.dto.response.SourceReference;
import com.documind.repository.projection.ChunkSimilarityProjection;
import org.springframework.stereotype.Component;

@Component
public class SourceReferenceMapper {

    private static final int EXCERPT_LENGTH = 300;

    public SourceReference toReference(ChunkSimilarityProjection chunk) {
        return new SourceReference(
                chunk.getDocumentId(),
                chunk.getDocumentName(),
                chunk.getChunkIndex(),
                roundToThreeDecimals(chunk.getSimilarity()),
                toExcerpt(chunk.getContent())
        );
    }

    private static String toExcerpt(String content) {
        return content.length() <= EXCERPT_LENGTH ? content : content.substring(0, EXCERPT_LENGTH) + "...";
    }

    private static double roundToThreeDecimals(Double value) {
        return value == null ? 0 : Math.round(value * 1000) / 1000.0;
    }
}

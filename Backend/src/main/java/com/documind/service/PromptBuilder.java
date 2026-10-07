package com.documind.service;

import com.documind.repository.projection.ChunkSimilarityProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    private static final String SYSTEM_PROMPT = """
            You are DocuMind, an assistant that answers questions about the user's documents.
            Answer using ONLY the information in the provided context.
            If the context does not contain the answer, say that the documents do not contain that information.
            Mention which source the information comes from, e.g. (Source 2).
            Be concise and clear.
            """;

    public String systemPrompt() {
        return SYSTEM_PROMPT;
    }

    public String buildUserPrompt(String question, List<ChunkSimilarityProjection> chunks) {
        StringBuilder prompt = new StringBuilder("Context:\n\n");
        for (int i = 0; i < chunks.size(); i++) {
            ChunkSimilarityProjection chunk = chunks.get(i);
            prompt.append("[Source ").append(i + 1).append(": ")
                    .append(chunk.getDocumentName())
                    .append(", part ").append(chunk.getChunkIndex() + 1).append("]\n")
                    .append(chunk.getContent())
                    .append("\n\n");
        }
        return prompt.append("Question: ").append(question).toString();
    }
}

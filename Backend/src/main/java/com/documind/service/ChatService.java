package com.documind.service;

import com.documind.config.RagProperties;
import com.documind.dto.request.ChatRequest;
import com.documind.dto.response.ChatResponse;
import com.documind.mapper.SourceReferenceMapper;
import com.documind.repository.projection.ChunkSimilarityProjection;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates the RAG flow: embed question, retrieve similar chunks, build prompt, generate answer.
 */
@Service
public class ChatService {

    private static final String NO_DOCUMENTS_ANSWER =
            "There are no processed documents yet. Upload a document and wait until it is READY, then ask again.";

    private final OllamaEmbeddingService embeddingService;
    private final VectorSearchService vectorSearchService;
    private final PromptBuilder promptBuilder;
    private final OllamaChatService chatService;
    private final SourceReferenceMapper sourceReferenceMapper;
    private final RagProperties ragProperties;

    public ChatService(OllamaEmbeddingService embeddingService,
                       VectorSearchService vectorSearchService,
                       PromptBuilder promptBuilder,
                       OllamaChatService chatService,
                       SourceReferenceMapper sourceReferenceMapper,
                       RagProperties ragProperties) {
        this.embeddingService = embeddingService;
        this.vectorSearchService = vectorSearchService;
        this.promptBuilder = promptBuilder;
        this.chatService = chatService;
        this.sourceReferenceMapper = sourceReferenceMapper;
        this.ragProperties = ragProperties;
    }

    public ChatResponse ask(ChatRequest request) {
        String question = request.question().trim();

        float[] questionEmbedding = embeddingService.embed(question);
        List<ChunkSimilarityProjection> chunks =
                vectorSearchService.findMostSimilarChunks(questionEmbedding, ragProperties.topK());

        if (chunks.isEmpty()) {
            return new ChatResponse(NO_DOCUMENTS_ANSWER, List.of());
        }

        String answer = chatService.generate(
                promptBuilder.systemPrompt(),
                promptBuilder.buildUserPrompt(question, chunks));

        return new ChatResponse(answer, chunks.stream().map(sourceReferenceMapper::toReference).toList());
    }
}

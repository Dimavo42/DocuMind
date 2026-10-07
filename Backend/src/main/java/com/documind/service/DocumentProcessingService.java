package com.documind.service;

import com.documind.config.AsyncConfig;
import com.documind.config.RagProperties;
import com.documind.entity.Document;
import com.documind.entity.DocumentChunk;
import com.documind.exception.DocumentProcessingException;
import com.documind.repository.DocumentChunkRepository;
import com.documind.repository.DocumentRepository;
import com.documind.service.extraction.TextExtractionService;
import com.documind.util.TextChunker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the ingestion pipeline in the background: extract text, chunk, embed, store, mark READY.
 */
@Service
public class DocumentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentProcessingService.class);

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final TextExtractionService textExtractionService;
    private final OllamaEmbeddingService embeddingService;
    private final RagProperties ragProperties;

    public DocumentProcessingService(DocumentRepository documentRepository,
                                     DocumentChunkRepository chunkRepository,
                                     TextExtractionService textExtractionService,
                                     OllamaEmbeddingService embeddingService,
                                     RagProperties ragProperties) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.textExtractionService = textExtractionService;
        this.embeddingService = embeddingService;
        this.ragProperties = ragProperties;
    }

    @Async(AsyncConfig.DOCUMENT_PROCESSING_EXECUTOR)
    public void process(Long documentId, byte[] content) {
        documentRepository.findById(documentId).ifPresent(document -> {
            try {
                ingest(document, content);
            } catch (RuntimeException ex) {
                log.error("Processing of document {} failed", documentId, ex);
                markFailed(documentId, ex.getMessage());
            }
        });
    }

    private void ingest(Document document, byte[] content) {
        document.markProcessing();
        document = documentRepository.save(document);

        String text = textExtractionService.extractText(document.getType(), content);
        List<String> chunkTexts = TextChunker.split(text, ragProperties.chunkSize(), ragProperties.chunkOverlap());
        if (chunkTexts.isEmpty()) {
            throw new DocumentProcessingException("No readable text was found in the document");
        }

        List<float[]> embeddings = embeddingService.embedAll(chunkTexts);
        chunkRepository.saveAll(toChunkEntities(document, chunkTexts, embeddings));

        document.markReady(chunkTexts.size());
        documentRepository.save(document);
        log.info("Document {} is READY with {} chunks", document.getId(), chunkTexts.size());
    }

    private static List<DocumentChunk> toChunkEntities(Document document, List<String> texts, List<float[]> embeddings) {
        List<DocumentChunk> chunks = new ArrayList<>(texts.size());
        for (int i = 0; i < texts.size(); i++) {
            chunks.add(new DocumentChunk(document, i, texts.get(i), embeddings.get(i)));
        }
        return chunks;
    }

    private void markFailed(Long documentId, String reason) {
        // The document may have been deleted while it was being processed.
        documentRepository.findById(documentId).ifPresent(document -> {
            document.markFailed(reason);
            documentRepository.save(document);
        });
    }
}

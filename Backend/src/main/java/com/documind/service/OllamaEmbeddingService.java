package com.documind.service;

import com.documind.client.ollama.OllamaClient;
import com.documind.client.ollama.dto.OllamaEmbedRequest;
import com.documind.client.ollama.dto.OllamaEmbedResponse;
import com.documind.config.OllamaProperties;
import com.documind.config.RagProperties;
import com.documind.exception.OllamaClientException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OllamaEmbeddingService {

    private final OllamaClient ollamaClient;
    private final OllamaProperties ollamaProperties;
    private final RagProperties ragProperties;

    public OllamaEmbeddingService(OllamaClient ollamaClient,
                                  OllamaProperties ollamaProperties,
                                  RagProperties ragProperties) {
        this.ollamaClient = ollamaClient;
        this.ollamaProperties = ollamaProperties;
        this.ragProperties = ragProperties;
    }

    public float[] embed(String text) {
        return embedBatch(List.of(text)).getFirst();
    }

    /**
     * Embeds all texts, sending them to Ollama in batches. The result order matches the input order.
     */
    public List<float[]> embedAll(List<String> texts) {
        List<float[]> embeddings = new ArrayList<>(texts.size());
        int batchSize = ragProperties.embeddingBatchSize();
        for (int from = 0; from < texts.size(); from += batchSize) {
            int to = Math.min(from + batchSize, texts.size());
            embeddings.addAll(embedBatch(texts.subList(from, to)));
        }
        return embeddings;
    }

    private List<float[]> embedBatch(List<String> texts) {
        OllamaEmbedResponse response = ollamaClient.embed(
                new OllamaEmbedRequest(ollamaProperties.embeddingModel(), texts));
        List<float[]> embeddings = response.embeddings();
        if (embeddings == null || embeddings.size() != texts.size()) {
            throw new OllamaClientException("Ollama returned an unexpected number of embeddings");
        }
        return embeddings;
    }
}

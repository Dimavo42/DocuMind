package com.documind.client.ollama;

import com.documind.client.ollama.dto.OllamaChatRequest;
import com.documind.client.ollama.dto.OllamaChatResponse;
import com.documind.client.ollama.dto.OllamaEmbedRequest;
import com.documind.client.ollama.dto.OllamaEmbedResponse;
import com.documind.exception.OllamaClientException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Thin HTTP wrapper around the Ollama REST API. All raw HTTP calls to Ollama live here.
 */
@Component
public class OllamaClient {

    private static final String EMBED_PATH = "/api/embed";
    private static final String CHAT_PATH = "/api/chat";

    private final RestClient ollamaRestClient;

    public OllamaClient(RestClient ollamaRestClient) {
        this.ollamaRestClient = ollamaRestClient;
    }

    public OllamaEmbedResponse embed(OllamaEmbedRequest request) {
        return post(EMBED_PATH, request, OllamaEmbedResponse.class);
    }

    public OllamaChatResponse chat(OllamaChatRequest request) {
        return post(CHAT_PATH, request, OllamaChatResponse.class);
    }

    private <T> T post(String path, Object body, Class<T> responseType) {
        try {
            T response = ollamaRestClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(responseType);
            if (response == null) {
                throw new OllamaClientException("Ollama returned an empty response for " + path);
            }
            return response;
        } catch (RestClientException ex) {
            throw new OllamaClientException("Ollama request to " + path + " failed: " + ex.getMessage(), ex);
        }
    }
}

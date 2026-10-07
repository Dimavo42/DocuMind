package com.documind.service;

import com.documind.client.ollama.OllamaClient;
import com.documind.client.ollama.dto.OllamaChatRequest;
import com.documind.client.ollama.dto.OllamaChatResponse;
import com.documind.client.ollama.dto.OllamaMessage;
import com.documind.config.OllamaProperties;
import com.documind.enums.ChatRole;
import com.documind.exception.OllamaClientException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OllamaChatService {

    private final OllamaClient ollamaClient;
    private final OllamaProperties ollamaProperties;

    public OllamaChatService(OllamaClient ollamaClient, OllamaProperties ollamaProperties) {
        this.ollamaClient = ollamaClient;
        this.ollamaProperties = ollamaProperties;
    }

    public String generate(String systemPrompt, String userPrompt) {
        OllamaChatRequest request = new OllamaChatRequest(
                ollamaProperties.chatModel(),
                List.of(new OllamaMessage(ChatRole.SYSTEM, systemPrompt),
                        new OllamaMessage(ChatRole.USER, userPrompt)),
                false);

        OllamaChatResponse response = ollamaClient.chat(request);
        if (response.message() == null || response.message().content() == null) {
            throw new OllamaClientException("Ollama returned a chat response without content");
        }
        return response.message().content().trim();
    }
}

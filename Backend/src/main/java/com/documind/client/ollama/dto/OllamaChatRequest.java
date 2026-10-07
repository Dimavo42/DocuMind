package com.documind.client.ollama.dto;

import java.util.List;

public record OllamaChatRequest(String model, List<OllamaMessage> messages, boolean stream) {
}

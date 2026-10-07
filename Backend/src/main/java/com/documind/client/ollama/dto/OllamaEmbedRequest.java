package com.documind.client.ollama.dto;

import java.util.List;

public record OllamaEmbedRequest(String model, List<String> input) {
}

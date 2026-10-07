package com.documind.client.ollama.dto;

import com.documind.enums.ChatRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaMessage(ChatRole role, String content) {
}

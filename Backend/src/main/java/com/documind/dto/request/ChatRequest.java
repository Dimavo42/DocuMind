package com.documind.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "must not be empty")
        @Size(max = 2000, message = "must be at most 2000 characters")
        String question
) {
}

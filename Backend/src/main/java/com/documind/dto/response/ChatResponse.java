package com.documind.dto.response;

import java.util.List;

public record ChatResponse(String answer, List<SourceReference> sources) {
}

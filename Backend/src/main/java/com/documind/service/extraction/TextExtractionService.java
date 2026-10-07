package com.documind.service.extraction;

import com.documind.enums.DocumentType;
import com.documind.exception.DocumentProcessingException;
import com.documind.util.TextNormalizer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Picks the right {@link TextExtractor} for a document type and returns normalized text.
 */
@Service
public class TextExtractionService {

    private final Map<DocumentType, TextExtractor> extractorsByType = new EnumMap<>(DocumentType.class);

    public TextExtractionService(List<TextExtractor> extractors) {
        extractors.forEach(extractor -> extractorsByType.put(extractor.supportedType(), extractor));
    }

    public boolean supports(DocumentType type) {
        return extractorsByType.containsKey(type);
    }

    public String extractText(DocumentType type, byte[] content) {
        TextExtractor extractor = extractorsByType.get(type);
        if (extractor == null) {
            throw new DocumentProcessingException("No text extractor available for type " + type);
        }
        try {
            return TextNormalizer.normalize(extractor.extract(content));
        } catch (IOException ex) {
            throw new DocumentProcessingException("Could not read the " + type + " file: " + ex.getMessage(), ex);
        }
    }
}

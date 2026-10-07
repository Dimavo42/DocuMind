package com.documind.service.extraction;

import com.documind.enums.DocumentType;

import java.io.IOException;

/**
 * Extracts plain text from a file of a specific {@link DocumentType}.
 */
public interface TextExtractor {

    DocumentType supportedType();

    String extract(byte[] content) throws IOException;
}

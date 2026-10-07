package com.documind.service.extraction;

import com.documind.enums.DocumentType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TxtTextExtractor implements TextExtractor {

    @Override
    public DocumentType supportedType() {
        return DocumentType.TXT;
    }

    @Override
    public String extract(byte[] content) {
        return new String(content, StandardCharsets.UTF_8);
    }
}

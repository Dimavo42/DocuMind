package com.documind.service.extraction;

import com.documind.enums.DocumentType;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class DocxTextExtractor implements TextExtractor {

    @Override
    public DocumentType supportedType() {
        return DocumentType.DOCX;
    }

    @Override
    public String extract(byte[] content) throws IOException {
        try (XWPFDocument docx = new XWPFDocument(new ByteArrayInputStream(content));
             XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
            return extractor.getText();
        }
    }
}

package com.documind.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentTypeTest {

    @Test
    void detectsTypeFromExtensionIgnoringCase() {
        assertThat(DocumentType.fromFilename("notes.PDF")).isEqualTo(DocumentType.PDF);
        assertThat(DocumentType.fromFilename("readme.txt")).isEqualTo(DocumentType.TXT);
        assertThat(DocumentType.fromFilename("report.docx")).isEqualTo(DocumentType.DOCX);
    }

    @Test
    void returnsUnknownForUnsupportedOrMissingExtension() {
        assertThat(DocumentType.fromFilename("image.png")).isEqualTo(DocumentType.UNKNOWN);
        assertThat(DocumentType.fromFilename("no-extension")).isEqualTo(DocumentType.UNKNOWN);
        assertThat(DocumentType.fromFilename(null)).isEqualTo(DocumentType.UNKNOWN);
    }
}

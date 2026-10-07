package com.documind.enums;

import java.util.Locale;

public enum DocumentType {
    PDF("pdf"),
    TXT("txt"),
    DOCX("docx"),
    UNKNOWN("");

    private final String extension;

    DocumentType(String extension) {
        this.extension = extension;
    }

    public static DocumentType fromFilename(String filename) {
        if (filename == null || !filename.contains(".")) {
            return UNKNOWN;
        }
        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        for (DocumentType type : values()) {
            if (type != UNKNOWN && type.extension.equals(fileExtension)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}

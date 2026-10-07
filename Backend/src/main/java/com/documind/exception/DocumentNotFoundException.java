package com.documind.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(Long documentId) {
        super("Document with id " + documentId + " was not found");
    }
}

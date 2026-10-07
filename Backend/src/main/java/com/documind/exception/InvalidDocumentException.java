package com.documind.exception;

/**
 * Thrown when an uploaded file is rejected, e.g. it is empty or its type is not supported.
 */
public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException(String message) {
        super(message);
    }
}

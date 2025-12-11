package org.divorobioff.nevis.assignment.rest.error.exception;

import java.util.UUID;

public class DocumentMissingException extends RuntimeException {

    public DocumentMissingException(UUID id) {
        super("Document with '%s' is missing".formatted(id.toString()));
    }
}

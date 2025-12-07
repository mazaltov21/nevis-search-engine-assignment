package org.divorobioff.nevis.assignment.rest.error.exception;

public class InvalidIdentifierException extends RuntimeException {

    public InvalidIdentifierException(String message) {
        super(message);
    }

    public InvalidIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.divorobioff.nevis.assignment.rest.error.exception;

public class ClientEmailAlreadyExistsException extends RuntimeException {

    public ClientEmailAlreadyExistsException(String email) {
        super("Client with email '%s' already exists".formatted(email));
    }
}

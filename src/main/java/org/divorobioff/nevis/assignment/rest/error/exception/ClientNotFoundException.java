package org.divorobioff.nevis.assignment.rest.error.exception;

import java.util.UUID;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(UUID id) {
        super("Client with id '%s' not found".formatted(id));
    }
}
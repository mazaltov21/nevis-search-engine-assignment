package org.divorobioff.nevis.assignment.rest;

import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.rest.api.ClientsApi;
import org.divorobioff.nevis.assignment.rest.error.exception.InvalidIdentifierException;
import org.divorobioff.nevis.assignment.service.client.api.ClientServiceApi;
import org.divorobioff.nevis.assignment.service.document.api.DocumentServiceApi;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ClientController implements ClientsApi {

    private final ClientServiceApi clientService;
    private final DocumentServiceApi documentService;

    public ClientController(ClientServiceApi clientService, DocumentServiceApi documentService) {
        this.clientService = clientService;
        this.documentService = documentService;
    }

    @Override
    public ClientResponseDto createClient(ClientRequestDto request) {
        return clientService.createClient(request);
    }

    @Override
    public DocumentResponseDto createClientDocument(String clientId, DocumentRequestDto request) {
        UUID clientUUID;
        try {
            clientUUID = UUID.fromString(clientId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidIdentifierException("Invalid client id format", ex);
        }
        return documentService.createClientDocument(clientUUID, request);
    }
}

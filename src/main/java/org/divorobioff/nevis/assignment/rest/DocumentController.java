package org.divorobioff.nevis.assignment.rest;

import org.divorobioff.nevis.assignment.dto.response.DocumentSummaryDto;
import org.divorobioff.nevis.assignment.rest.api.DocumentsApi;
import org.divorobioff.nevis.assignment.rest.error.exception.InvalidIdentifierException;
import org.divorobioff.nevis.assignment.service.document.api.DocumentServiceApi;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DocumentController implements DocumentsApi {

    private final DocumentServiceApi documentService;

    public DocumentController(DocumentServiceApi documentService) {
        this.documentService = documentService;
    }

    @Override
    public DocumentSummaryDto summary(String documentId) {
        UUID documentUUID;
        try {
            documentUUID = UUID.fromString(documentId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidIdentifierException("Invalid document id format", ex);
        }
        return documentService.documentSummary(documentUUID);
    }
}

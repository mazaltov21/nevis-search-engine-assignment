package org.divorobioff.nevis.assignment.service.document.api;

import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;

import java.util.UUID;

public interface DocumentServiceApi {

    DocumentResponseDto createClientDocument(UUID clientId, DocumentRequestDto request);
}

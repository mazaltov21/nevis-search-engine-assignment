package org.divorobioff.nevis.assignment.service.document;

import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.divorobioff.nevis.assignment.mapper.DocumentMapper;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientNotFoundException;
import org.divorobioff.nevis.assignment.service.document.api.DocumentIndexServiceApi;
import org.divorobioff.nevis.assignment.service.document.api.DocumentServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DocumentService implements DocumentServiceApi {

    private final ClientRepository clientRepository;
    private final DocumentRepository documentRepository;
    private final DocumentIndexServiceApi documentIndexService;

    public DocumentService (
            ClientRepository clientRepository,
            DocumentRepository documentRepository,
            DocumentIndexServiceApi documentIndexService
    ) {
        this.clientRepository = clientRepository;
        this.documentRepository = documentRepository;
        this.documentIndexService = documentIndexService;
    }

    @Override
    public DocumentResponseDto createClientDocument(UUID clientId, DocumentRequestDto request) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        DocumentEntity document = DocumentMapper.toEntity(request);
        document.setClient(client);

        DocumentEntity saved = documentRepository.save(document);
        documentIndexService.addVectorIndex(saved);
        return DocumentMapper.toDto(saved);
    }
}

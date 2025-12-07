package org.divorobioff.nevis.assignment.service;

import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.divorobioff.nevis.assignment.mapper.DocumentMapper;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientNotFoundException;
import org.divorobioff.nevis.assignment.service.api.DocumentServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DocumentService implements DocumentServiceApi {

    private final ClientRepository clientRepository;
    private final DocumentRepository documentRepository;

    public DocumentService (ClientRepository clientRepository, DocumentRepository documentRepository) {
        this.clientRepository = clientRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public DocumentResponseDto createClientDocument(UUID clientId, DocumentRequestDto request) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        DocumentEntity document = DocumentMapper.toEntity(request);
        document.setClient(client);

        DocumentEntity saved = documentRepository.save(document);
        return DocumentMapper.toDto(saved);
    }
}

package org.divorobioff.nevis.assignment.service.document;

import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentSummaryDto;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.divorobioff.nevis.assignment.mapper.DocumentMapper;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientNotFoundException;
import org.divorobioff.nevis.assignment.rest.error.exception.DocumentMissingException;
import org.divorobioff.nevis.assignment.service.document.api.DocumentIndexServiceApi;
import org.divorobioff.nevis.assignment.service.document.api.DocumentServiceApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DocumentService implements DocumentServiceApi {

    private final ClientRepository clientRepository;
    private final DocumentRepository documentRepository;
    private final DocumentIndexServiceApi documentIndexService;
    private final ChatClient openAiClient;

    public DocumentService (
            ClientRepository clientRepository,
            DocumentRepository documentRepository,
            DocumentIndexServiceApi documentIndexService,
            ChatClient.Builder chatClientBuilder
    ) {
        this.clientRepository = clientRepository;
        this.documentRepository = documentRepository;
        this.documentIndexService = documentIndexService;
        this.openAiClient = chatClientBuilder.build();
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

    @Override
    public DocumentSummaryDto documentSummary(UUID documentId) {
        DocumentEntity document = documentRepository.findDocumentEntityById(documentId)
                .orElseThrow(() -> new DocumentMissingException(documentId));
        String content = document.getTitle() + " " + document.getContent();
        return new DocumentSummaryDto(openAiClient.prompt()
                .system("AI assistant that provides quick summaries of documents.")
                .user("Provide a quick summary of the following document content: " + content)
                .call()
                .content()
        );
    }
}

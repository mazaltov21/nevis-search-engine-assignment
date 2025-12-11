package org.divorobioff.nevis.assignment.service.document;

import org.divorobioff.nevis.assignment.WireMockTestConfig;
import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentSummaryDto;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientNotFoundException;
import org.divorobioff.nevis.assignment.rest.error.exception.DocumentMissingException;
import org.divorobioff.nevis.assignment.service.client.api.ClientServiceApi;
import org.divorobioff.nevis.assignment.service.document.api.DocumentServiceApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class DocumentServiceIntegrationTest {

    @Autowired
    private DocumentServiceApi documentService;
    @Autowired
    private ClientServiceApi clientService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ChatClient.Builder chatClient;

    @MockitoBean
    private VectorStore vectorStore;

    @AfterEach
    void cleanUp() {
        documentRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("createClientDocument should persist document and return response with generated id")
    void createClientDocument_success() {
        ClientRequestDto clientRequest = new ClientRequestDto(
                "Alice",
                "Cooper",
                "alice.cooper@neviswealth.com",
                "UK"
        );

        ClientResponseDto clientResponse = clientService.createClient(clientRequest);
        UUID clientId = UUID.fromString(clientResponse.id());

        DocumentRequestDto documentRequest = new DocumentRequestDto(
                "Proof of address",
                "Utility bill for January 350 EUR"
        );

        DocumentResponseDto response =
                documentService.createClientDocument(clientId, documentRequest);

        assertThat(response.id()).isNotNull();
        assertThat(response.clientId()).isEqualTo(clientId.toString());
        assertThat(response.title()).isEqualTo("Proof of address");
        assertThat(response.content()).isEqualTo("Utility bill for January 350 EUR");
        assertThat(response.createdAt()).isNotNull();

        List<DocumentEntity> docs = documentRepository.findAll();
        assertThat(docs).hasSize(1);

        DocumentEntity stored = docs.getFirst();
        assertThat(stored.getTitle()).isEqualTo("Proof of address");
        assertThat(stored.getContent()).isEqualTo("Utility bill for January 350 EUR");
        assertThat(stored.getClient().getId()).isEqualTo(clientId);
        assertThat(stored.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("createClientDocument should throw when client does not exist")
    void createClientDocument_clientNotFound_throws() {
        UUID randomId = UUID.randomUUID();

        DocumentRequestDto request = new DocumentRequestDto(
                "Proof of address",
                "Utility bill"
        );

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> documentService.createClientDocument(randomId, request)
        );

        assertThat(ex.getMessage()).contains(randomId.toString());
        assertThat(documentRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("documentSummary returns summary from OpenAI client when document exists")
    void documentSummary_success() {
        ClientRequestDto clientRequest = new ClientRequestDto(
                "Alice",
                "Cooper",
                "alice.cooper@neviswealth.com",
                "UK"
        );

        ClientResponseDto clientResponse = clientService.createClient(clientRequest);
        UUID clientId = UUID.fromString(clientResponse.id());

        DocumentRequestDto documentRequest = new DocumentRequestDto(
                "Proof of address",
                "Utility bill for January 350 EUR"
        );

        DocumentResponseDto documentResponse =
                documentService.createClientDocument(clientId, documentRequest);
        UUID documentId = UUID.fromString(documentResponse.id());

        DocumentSummaryDto summary = documentService.documentSummary(documentId);

        assertThat(summary).isNotNull();
        assertThat(summary.summary())
                .isEqualTo("Mocked AI response");
    }

    @Test
    @DisplayName("documentSummary throws DocumentMissingException when document is not found")
    void documentSummary_documentNotFound_throws() {
        UUID documentId = UUID.randomUUID();

        DocumentMissingException ex = assertThrows(
                DocumentMissingException.class,
                () -> documentService.documentSummary(documentId)
        );

        assertThat(ex.getMessage()).contains(documentId.toString());
    }
}

package org.divorobioff.nevis.assignment.service;

import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class DocumentServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ClientService clientService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ClientRepository clientRepository;
    @AfterEach
    void cleanUp() {
        documentRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("createClientDocument should throw when client does not exist")
    void createDocument_clientNotFound_throws() {
        UUID randomId = UUID.randomUUID();
        DocumentRequestDto request = new DocumentRequestDto(
                "KYC document",
                "Some content"
        );

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> documentService.createClientDocument(randomId, request)
        );

        assertThat(ex.getMessage()).contains(randomId.toString());
    }

    @Test
    @DisplayName("createClientDocument should persist document and assign createdAt")
    void createDocument_success() {
        ClientRequestDto clientRequest = new ClientRequestDto(
                "Alice",
                "Cooper",
                "alice.cooper@neviswealth.com",
                "UK"
        );
        ClientResponseDto client = clientService.createClient(clientRequest);
        UUID clientId = UUID.fromString(client.id());

        DocumentRequestDto docRequest = new DocumentRequestDto(
                "Proof of address",
                "Utility bill from October"
        );

        DocumentResponseDto response = documentService.createClientDocument(clientId, docRequest);

        assertThat(response.id()).isNotNull();
        assertThat(response.clientId()).isEqualTo(client.id());
        assertThat(response.title()).isEqualTo("Proof of address");
        assertThat(response.createdAt()).isNotNull();

        UUID docId = UUID.fromString(response.id());
        DocumentEntity entity = documentRepository.findById(docId).orElseThrow();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(entity.getClient().getId()).isEqualTo(clientId);
    }
}

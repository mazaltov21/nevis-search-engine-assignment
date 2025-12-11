package org.divorobioff.nevis.assignment.service.document;

import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DocumentIndexServiceTest {

    @Mock
    private VectorStore vectorStore;
    private DocumentIndexService documentIndexService;

    @BeforeEach
    void setUp() {
        documentIndexService = new DocumentIndexService(vectorStore);
    }

    @Test
    @DisplayName("addVectorIndex builds Document with correct text and metadata and delegates to VectorStore")
    void addVectorIndex_success() {
        UUID documentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ClientEntity client = new ClientEntity();
        client.setId(clientId);

        DocumentEntity document = new DocumentEntity();
        document.setId(documentId);
        document.setClient(client);
        document.setTitle("Monthly payment");
        document.setContent("Utility bill for January 350 EUR");
        document.setCreatedAt(createdAt);

        documentIndexService.addVectorIndex(document);

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(1)).add(captor.capture());

        List<Document> docs = captor.getValue();
        assertThat(docs).hasSize(1);

        Document stored = docs.getFirst();

        assertThat(stored.getText())
                .isEqualTo("Utility bill for January 350 EUR");

        Map<String, Object> metadata = stored.getMetadata();
        assertThat(metadata)
                .containsEntry("id", documentId.toString())
                .containsEntry("clientId", clientId.toString())
                .containsEntry("title", "Monthly payment")
                .containsEntry("createdAt", createdAt.toString());
    }

    @Test
    @DisplayName("addVectorIndex does nothing special for null/blank content beyond passing it through")
    void addVectorIndex_handlesBlankContent() {
        UUID documentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        ClientEntity client = new ClientEntity();
        client.setId(clientId);

        DocumentEntity document = new DocumentEntity();
        document.setId(documentId);
        document.setClient(client);
        document.setTitle("Empty content");
        document.setContent(""); // blank content
        document.setCreatedAt(createdAt);

        documentIndexService.addVectorIndex(document);

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore, times(1)).add(captor.capture());

        Document stored = captor.getValue().getFirst();

        assertThat(stored.getText()).isEqualTo("");

        Map<String, Object> metadata = stored.getMetadata();
        assertThat(metadata)
                .containsEntry("id", documentId.toString())
                .containsEntry("clientId", clientId.toString())
                .containsEntry("title", "Empty content")
                .containsEntry("createdAt", createdAt.toString());
    }
}

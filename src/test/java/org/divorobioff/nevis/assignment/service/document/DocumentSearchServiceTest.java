package org.divorobioff.nevis.assignment.service.document;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentSearchServiceTest {

    @Mock
    private VectorStore vectorStore;
    private DocumentSearchService documentSearchService;

    @BeforeEach
    void setUp() {
        documentSearchService = new DocumentSearchService(vectorStore);
        ReflectionTestUtils.setField(documentSearchService, "minSimilarity", 0.8d);
        ReflectionTestUtils.setField(documentSearchService, "defaultLimit", 5);
    }

    @Test
    @DisplayName("searchDocuments(null) should return empty list and not call vectorStore")
    void searchDocuments_nullQuery_returnsEmpty() {
        List<SearchResult.DocumentResult> results = documentSearchService.searchDocuments(null);

        assertThat(results).isEmpty();
        verifyNoInteractions(vectorStore);
    }

    @Test
    @DisplayName("searchDocuments with blank query should return empty list and not call vectorStore")
    void searchDocuments_blankQuery_returnsEmpty() {
        List<SearchResult.DocumentResult> results = documentSearchService.searchDocuments("   ");

        assertThat(results).isEmpty();
        verifyNoInteractions(vectorStore);
    }

    @Test
    @DisplayName("searchDocuments with valid query builds SearchRequest and maps results")
    void searchDocuments_validQuery_delegatesAndMaps() {
        UUID docId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        Map<String, Object> metadata = Map.of(
                "id", docId.toString(),
                "clientId", clientId.toString(),
                "title", "monthly payment",
                "createdAt", createdAt.toString()
        );

        Document vectorDoc = new Document(
                "utility bill for january 350 euro",
                metadata
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(vectorDoc));

        List<SearchResult.DocumentResult> results =
                documentSearchService.searchDocuments("address proof");

        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore, times(1)).similaritySearch(captor.capture());

        SearchRequest request = captor.getValue();
        assertThat(request.getQuery()).isEqualTo("address proof");
        assertThat(request.getTopK()).isEqualTo(5);
        assertThat(request.getSimilarityThreshold()).isEqualTo(0.8d);

        assertThat(results).hasSize(1);
        SearchResult.DocumentResult result = results.getFirst();

        assertThat(result.id()).isEqualTo(docId.toString());
        assertThat(result.clientId()).isEqualTo(clientId.toString());
        assertThat(result.title()).isEqualTo("monthly payment");
        assertThat(result.createdAt()).isEqualTo(createdAt);
        assertThat(result.content())
                .isEqualTo("utility bill for january 350 euro");
    }

    @Test
    @DisplayName("searchDocuments should handle missing metadata gracefully (nulls)")
    void searchDocuments_missingMetadata_allowsNulls() {
        Document vectorDoc = new Document("orphan doc", Map.of());
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(vectorDoc));

        List<SearchResult.DocumentResult> results =
                documentSearchService.searchDocuments("something");

        assertThat(results).hasSize(1);
        SearchResult.DocumentResult result = results.getFirst();

        assertThat(result.id()).isNull();
        assertThat(result.clientId()).isNull();
        assertThat(result.title()).isNull();
        assertThat(result.createdAt()).isNull();
        assertThat(result.content()).isEqualTo("orphan doc");
    }
}

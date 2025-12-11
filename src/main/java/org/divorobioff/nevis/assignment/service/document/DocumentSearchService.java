package org.divorobioff.nevis.assignment.service.document;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.service.document.api.DocumentSearchServiceApi;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DocumentSearchService implements DocumentSearchServiceApi {

    private final VectorStore vectorStore;

    @Value("${search.config.min-similarity}")
    private double minSimilarity;
    @Value("${search.config.default-limit}")
    private int defaultLimit;

    public DocumentSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public List<SearchResult.DocumentResult> searchDocuments(String query) {
        if (query == null || query.trim().isBlank()) {
            return List.of();
        }
        SearchRequest request = SearchRequest
                .builder()
                .query(query)
                .topK(defaultLimit)
                .similarityThreshold(minSimilarity)
                .build();
        List<Document> vectors = vectorStore.similaritySearch(request);
        return vectors.stream()
                .map(this::toSearchResult)
                .toList();
    }

    private SearchResult.DocumentResult toSearchResult(Document vector) {
        Map<String, Object> meta = vector.getMetadata();
        String id = (String) meta.getOrDefault("id", null);
        String clientId = (String) meta.getOrDefault("clientId", null);
        String title = (String) meta.getOrDefault("title", null);

        LocalDateTime createdAt = null;
        Object created = meta.get("createdAt");
        if (created instanceof String createdStr) {
            createdAt = LocalDateTime.parse(createdStr);
        }

        return new SearchResult.DocumentResult(
                id,
                clientId,
                title,
                vector.getText(),
                createdAt
        );
    }
}

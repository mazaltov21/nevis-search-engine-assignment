package org.divorobioff.nevis.assignment.service.document;

import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.service.document.api.DocumentIndexServiceApi;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DocumentIndexService implements DocumentIndexServiceApi {

    private final VectorStore vectorStore;

    public DocumentIndexService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void addVectorIndex(DocumentEntity document) {
        Map<String, Object> metadata = getMetadata(document);
        Document vector = new Document(document.getContent(), metadata);
        vectorStore.add(List.of(vector));
    }

    private Map<String, Object> getMetadata(DocumentEntity document) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", Objects.requireNonNull(document.getId()).toString());
        metadata.put("clientId", Objects.requireNonNull(document.getClient().getId()).toString());
        metadata.put("title", Objects.requireNonNull(document.getTitle()));
        metadata.put("createdAt", Objects.requireNonNull(document.getCreatedAt()).toString());
        return metadata;
    }
}

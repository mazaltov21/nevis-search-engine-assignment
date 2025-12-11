package org.divorobioff.nevis.assignment.service;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.service.client.api.ClientSearchServiceApi;
import org.divorobioff.nevis.assignment.service.document.DocumentSearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService implements SearchServiceApi {

    private final ClientSearchServiceApi clientSearchService;
    private final DocumentSearchService documentSearchService;

    public SearchService(ClientSearchServiceApi clientSearchService, DocumentSearchService documentSearchService) {
        this.clientSearchService = clientSearchService;
        this.documentSearchService = documentSearchService;
    }

    @Override
    public List<SearchResult> search(String q) {
        List<SearchResult> results = new ArrayList<>();
        results.addAll(clientSearchService.searchClients(q));
        results.addAll(documentSearchService.searchDocuments(q));
        return results;
    }
}

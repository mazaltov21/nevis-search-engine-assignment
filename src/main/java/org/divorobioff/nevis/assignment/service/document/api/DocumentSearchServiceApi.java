package org.divorobioff.nevis.assignment.service.document.api;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;

import java.util.List;

public interface DocumentSearchServiceApi {

    List<SearchResult.DocumentResult> searchDocuments(String query);
}

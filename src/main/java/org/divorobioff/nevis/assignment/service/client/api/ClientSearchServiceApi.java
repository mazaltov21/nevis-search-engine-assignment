package org.divorobioff.nevis.assignment.service.client.api;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;

import java.util.List;

public interface ClientSearchServiceApi {

    List<SearchResult.ClientResult> searchClients(String query);
}

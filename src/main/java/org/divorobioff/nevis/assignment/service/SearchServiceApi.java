package org.divorobioff.nevis.assignment.service;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;

import java.util.List;

public interface SearchServiceApi {

    List<SearchResult> search(String q);
}

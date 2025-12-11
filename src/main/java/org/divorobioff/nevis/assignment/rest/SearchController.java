package org.divorobioff.nevis.assignment.rest;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.rest.api.SearchApi;
import org.divorobioff.nevis.assignment.service.SearchServiceApi;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController implements SearchApi {

    private final SearchServiceApi searchService;

    public SearchController(SearchServiceApi searchService) {
        this.searchService = searchService;
    }

    @Override
    public List<SearchResult> search(String q) {
        return searchService.search(q);
    }
}

package org.divorobioff.nevis.assignment.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Validated
@RequestMapping("/search")
public interface SearchApi {

    @Operation(
            summary = "Search clients and documents",
            description = """
                Full-text style search across clients and documents.
                For now, this searches clients by their corporate email domains.
                Example: "Nevis Wealth" will match clients with email "firstname.lastname@neviswealth.com".
                """
    )
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping
    List<SearchResult> search(@RequestParam("q") String q);
}

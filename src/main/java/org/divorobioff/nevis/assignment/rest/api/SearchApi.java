package org.divorobioff.nevis.assignment.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.dto.response.error.ApiErrorResponse;
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
                Unified search endpoint across clients and documents.
                Client search:
                - Matches clients by corporate email domain (e.g. "Nevis Wealth" -> "firstname.lastname@neviswealth.com").
                - Query is normalized (case-insensitive; punctuation removed).
                Document search (semantic / LLM-based):
                - Uses embeddings + vector similarity search to find semantically related documents.
                - Example: searching for "address proof" can match documents containing "utility bill".
                - Results are returned as a mixed list of search result objects: client results and document results.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping
    List<SearchResult> search(
            @Parameter(
                    description = """
                        Search query string.
                        Examples:
                        - "Nevis Wealth" (matches clients by corporate domain)
                        - "address proof" (semantic document match, e.g. utility bill)
                        """,
                    example = "Nevis Wealth",
                    required = true
            )
            @RequestParam("q") String q);
}

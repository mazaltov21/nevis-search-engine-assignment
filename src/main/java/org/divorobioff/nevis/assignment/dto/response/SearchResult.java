package org.divorobioff.nevis.assignment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        description = "Search result (either client or document)",
        oneOf = { SearchResult.ClientResult.class, SearchResult.DocumentResult.class }
)
public sealed interface SearchResult permits SearchResult.ClientResult, SearchResult.DocumentResult {

    SearchResultType type();

    @Schema(name = "ClientSearchResult", description = "Client search result")
    record ClientResult(
            SearchResultType type,
            String id,
            String firstName,
            String lastName,
            String email,
            String countryOfResidence
    ) implements SearchResult {
        public ClientResult(String id, String firstName, String lastName, String email, String countryOfResidence) {
            this(SearchResultType.CLIENT, id, firstName, lastName, email, countryOfResidence);
        }
    }

    @Schema(name = "DocumentSearchResult", description = "Document search result")
    record DocumentResult(
            SearchResultType type,
            String id,
            String clientId,
            String title,
            String content,
            LocalDateTime createdAt
    ) implements SearchResult {
        public DocumentResult(String id, String clientId, String title, String content, LocalDateTime createdAt) {
            this(SearchResultType.DOCUMENT, id, clientId, title, content, createdAt);
        }
    }
}

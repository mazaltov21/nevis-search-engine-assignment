package org.divorobioff.nevis.assignment.dto.response;

import java.time.LocalDateTime;

public sealed interface SearchResult permits SearchResult.ClientResult, SearchResult.DocumentResult {

    SearchResultType type();

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

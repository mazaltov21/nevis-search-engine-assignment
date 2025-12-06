package org.divorobioff.nevis.assignment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record DocumentResponseDto(
        String id,
        @JsonProperty("client_id")
        String clientId,
        String title,
        String content,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}

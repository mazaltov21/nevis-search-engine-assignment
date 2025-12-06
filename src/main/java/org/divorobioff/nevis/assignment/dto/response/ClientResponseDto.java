package org.divorobioff.nevis.assignment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientResponseDto(
        String id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String email,
        String countryOfResidence
) {
}

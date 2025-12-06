package org.divorobioff.nevis.assignment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequestDto(
        @NotBlank
        @JsonProperty("first_name")
        String firstName,
        @NotBlank
        @JsonProperty("last_name")
        String lastName,
        @NotBlank
        @Email
        String email,
        String countryOfResidence
) {
}

package org.divorobioff.nevis.assignment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DocumentRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String content
) {
}

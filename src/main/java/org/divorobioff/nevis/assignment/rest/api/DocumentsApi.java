package org.divorobioff.nevis.assignment.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.divorobioff.nevis.assignment.dto.response.DocumentSummaryDto;
import org.divorobioff.nevis.assignment.dto.response.error.ApiErrorResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/documents")
public interface DocumentsApi {

    @Operation(
            summary = "Get document summary",
            description = """
                Generates a short AI-powered summary for a document.
                The document must already exist in the system.
                The summary is generated using an LLM based on the document title and content.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Document summary successfully generated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DocumentSummaryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid document id format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Document not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/summary/{id}")
    DocumentSummaryDto summary(
            @Parameter(
                    description = "Document UUID",
                    example = "2bc9a89d-def4-4396-bb38-6e8cafc9b04f",
                    required = true
            )
            @PathVariable("id") String documentId);
}

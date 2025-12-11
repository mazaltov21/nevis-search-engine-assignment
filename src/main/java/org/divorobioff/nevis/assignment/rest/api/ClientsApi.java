package org.divorobioff.nevis.assignment.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.dto.response.error.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/clients")
public interface ClientsApi {

    @Operation(
            summary = "Create a new client",
            description = """
                Creates and persists a new client.
                Constraints:
                - Email address must be unique.
                - Email must be a valid corporate email.
                On success, returns the newly created client including its generated UUID.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Client successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Client with the same email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ClientResponseDto createClient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Client creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClientRequestDto.class))
            )
            @Valid @RequestBody ClientRequestDto request);


    @Operation(
            summary = "Create a document for a client",
            description = """
                Creates a new document and attaches it to an existing client.
                Constraints:
                - Client must exist.
                - Document content must not be empty.
                The document is stored with a creation timestamp and indexed for semantic search.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Document successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DocumentResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid client id format or invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping("/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    DocumentResponseDto createClientDocument(
            @Parameter(
                    description = "Client UUID",
                    example = "bd55e1e1-a78a-4545-9e67-c8346708aa68",
                    required = true
            )
            @PathVariable("id") String clientId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Document creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DocumentRequestDto.class))
            )
            @Valid @RequestBody DocumentRequestDto request);
}

package org.divorobioff.nevis.assignment.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/clients")
public interface ClientsApi {

    @Operation(summary = "Create a new client API",
               description = """
                       Creates and saves a new client in DB.
                       The email address must be unique.
                       If the transaction completed successfully client ID returned in the response.
                       """)
    @ApiResponse(responseCode = "201", description = "Client successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "409", description = "Client with the same email already exists")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ClientResponseDto createClient(@Valid @RequestBody ClientRequestDto request);

    @Operation(summary = "Create a document for a client API",
               description = """
                    Creates a new document and attach it to an existing client.
                    The client must already exist.
                    The document is stored with a creation timestamp.
                    """)
    @ApiResponse(responseCode = "201", description = "Document successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "404", description = "Client not found")
    @PostMapping("/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    DocumentResponseDto createClientDocument(@PathVariable("id") String clientId, @Valid @RequestBody DocumentRequestDto request);
}

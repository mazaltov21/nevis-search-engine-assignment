package org.divorobioff.nevis.assignment.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/clients")
public interface ClientsApi {

    @Operation(summary = "Create client", description = "Create a new client")
    @ApiResponse(responseCode = "201", description = "Description TBD")
    @PostMapping
    ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientRequestDto request);

    @Operation(summary = "Create document for client", description = "Create a new document for a client")
    @ApiResponse(responseCode = "201", description = "Description TBD")
    @PostMapping("/{id}/documents")
    ResponseEntity<DocumentResponseDto> createClientDocument(@PathVariable("id") String clientId, @Valid @RequestBody DocumentRequestDto request);
}

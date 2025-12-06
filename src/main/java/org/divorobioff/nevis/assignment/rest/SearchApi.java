package org.divorobioff.nevis.assignment.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
public interface SearchApi {

    @Operation(summary = "Search", description = "Search endpoint")
    @ApiResponse(responseCode = "200", description = "Description TBD")
    @GetMapping("/search")
    ResponseEntity<Object> search(@RequestParam("q") String query);
}

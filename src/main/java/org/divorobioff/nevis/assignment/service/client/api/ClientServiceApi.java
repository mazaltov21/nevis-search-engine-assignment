package org.divorobioff.nevis.assignment.service.client.api;

import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;

import java.util.UUID;

public interface ClientServiceApi {

    ClientResponseDto createClient(ClientRequestDto request);

    ClientResponseDto getClient(UUID id);
}

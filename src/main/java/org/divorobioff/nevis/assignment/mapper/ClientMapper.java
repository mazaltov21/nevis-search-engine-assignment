package org.divorobioff.nevis.assignment.mapper;

import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.entity.ClientEntity;

public final class ClientMapper {

    private ClientMapper() {}

    public static ClientEntity toEntity(ClientRequestDto dto) {
        ClientEntity entity = new ClientEntity();
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setEmail(dto.email());
        entity.setCountryOfResidence(dto.countryOfResidence());
        return entity;
    }

    public static ClientResponseDto toDto(ClientEntity entity) {
        return new ClientResponseDto(
                entity.getId().toString(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getCountryOfResidence()
        );
    }
}

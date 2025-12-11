package org.divorobioff.nevis.assignment.service.client;

import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.mapper.ClientMapper;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientEmailAlreadyExistsException;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientNotFoundException;
import org.divorobioff.nevis.assignment.service.client.api.ClientServiceApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ClientService implements ClientServiceApi {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDto createClient(ClientRequestDto request) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new ClientEmailAlreadyExistsException(request.email());
        }

        ClientEntity entity = ClientMapper.toEntity(request);
        ClientEntity saved = clientRepository.save(entity);
        return ClientMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto getClient(UUID id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return ClientMapper.toDto(client);
    }
}

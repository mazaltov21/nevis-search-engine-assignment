package org.divorobioff.nevis.assignment.service;

import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.response.ClientResponseDto;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientEmailAlreadyExistsException;
import org.divorobioff.nevis.assignment.rest.error.exception.ClientNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class ClientServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRepository clientRepository;
    @AfterEach
    void cleanUp() {
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("createClient should persist client and return response with generated id")
    void createClient_success() {
        ClientRequestDto request = new ClientRequestDto(
                "Alice",
                "Cooper",
                "alice.cooper@neviswealth.com",
                "UK"
        );
        ClientResponseDto response = clientService.createClient(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("alice.cooper@neviswealth.com");

        UUID id = UUID.fromString(response.id());
        ClientEntity entity = clientRepository.findById(id).orElseThrow();
        assertThat(entity.getFirstName()).isEqualTo("Alice");
        assertThat(entity.getCountryOfResidence()).isEqualTo("UK");
    }

    @Test
    @DisplayName("createClient should throw when email already exists")
    void createClient_duplicateEmail_throws() {
        ClientRequestDto first = new ClientRequestDto(
                "Alice",
                "Cooper",
                "alice.cooper@neviswealth.com",
                "UK"
        );
        clientService.createClient(first);

        ClientRequestDto second = new ClientRequestDto(
                "Bob",
                "Smith",
                "alice.cooper@neviswealth.com",
                "NL"
        );

        ClientEmailAlreadyExistsException ex = assertThrows(
                ClientEmailAlreadyExistsException.class,
                () -> clientService.createClient(second)
        );

        assertThat(ex.getMessage()).contains("alice.cooper@neviswealth.com");
    }

    @Test
    @DisplayName("getClient should throw when client does not exist")
    void getClient_notFound_throws() {
        UUID randomId = UUID.randomUUID();

        ClientNotFoundException ex = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClient(randomId)
        );

        assertThat(ex.getMessage()).contains(randomId.toString());
    }
}

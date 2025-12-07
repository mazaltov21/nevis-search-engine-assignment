package org.divorobioff.nevis.assignment.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.divorobioff.nevis.assignment.dto.request.ClientRequestDto;
import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ClientsControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @AfterEach
    void cleanUp() {
        documentRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /clients should return 201 and client response")
    void createClient_httpSuccess() throws Exception {
        ClientRequestDto request = new ClientRequestDto(
                "Alice",
                "Cooper",
                "alice.cooper@neviswealth.com",
                "UK"
        );

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyString())))
                .andExpect(jsonPath("$.email").value("alice.cooper@neviswealth.com"));
    }

    @Test
    @DisplayName("POST /clients should return 400 when email is invalid")
    void createClient_invalidEmail_returnsBadRequest() throws Exception {
        ClientRequestDto request = new ClientRequestDto(
                "Alice",
                "Cooper",
                "random-test",
                "CH"
        );

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("email")));
    }

    @Test
    @DisplayName("POST /clients/{id}/documents should return 400 when id is not UUID")
    void createDocument_invalidClientId_returnsBadRequest() throws Exception {
        DocumentRequestDto request = new DocumentRequestDto(
                "Proof of address",
                "Utility bill"
        );

        mockMvc.perform(post("/clients/not-a-uuid/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Invalid client id format")));
    }

    @Test
    @DisplayName("POST /clients/{id}/documents should return 404 when client not found")
    void createDocument_clientNotFound_returnsNotFound() throws Exception {
        DocumentRequestDto request = new DocumentRequestDto(
                "Proof of address",
                "Utility bill"
        );

        String randomId = "00000000-0000-0000-0000-000000000001";

        mockMvc.perform(post("/clients/{id}/documents", randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("not found")));
    }
}

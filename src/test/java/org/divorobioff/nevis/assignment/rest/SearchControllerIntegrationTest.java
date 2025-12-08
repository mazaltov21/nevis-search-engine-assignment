package org.divorobioff.nevis.assignment.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class SearchControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /search?q=Nevis should return client results for nevis.com and neviswealth.com")
    void httpSearch_nevis_returnsExpectedClients() throws Exception {
        ClientEntity nevisWealth = new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@neviswealth.com",
                "CH"
        );
        ClientEntity nevis = new ClientEntity(
                "Alice",
                "Nevis",
                "alice@nevis.com",
                "CH"
        );
        clientRepository.save(nevisWealth);
        clientRepository.save(nevis);

        mockMvc.perform(get("/search")
                        .param("q", "Nevis")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].type", containsInAnyOrder("client", "client")))
                .andExpect(jsonPath("$[*].email",
                        containsInAnyOrder("john.wealth@neviswealth.com", "alice@nevis.com")));
    }

    @Test
    @DisplayName("GET /search?q=wealth should return client results for neviswealth.com only")
    void httpSearch_wealth_returnsOnlyNevisWealth() throws Exception {
        ClientEntity nevisWealth = new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@neviswealth.com",
                "CH"
        );
        ClientEntity nevis = new ClientEntity(
                "Alice",
                "Nevis",
                "alice@nevis.com",
                "CH"
        );
        clientRepository.save(nevisWealth);
        clientRepository.save(nevis);

        mockMvc.perform(get("/search")
                        .param("q", "wealth")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("client"))
                .andExpect(jsonPath("$[0].email").value("john.wealth@neviswealth.com"));
    }

    @Test
    @DisplayName("GET /search normalizes query with punctuation and mixed case")
    void httpSearch_normalizesQuery() throws Exception {
        ClientEntity nevisWealth = new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@NevisWealth.Com",
                "CH"
        );
        clientRepository.save(nevisWealth);

        mockMvc.perform(get("/search")
                        .param("q", "  nEvIs---WEALTH!!! ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("client"))
                .andExpect(jsonPath("$[0].email").value("john.wealth@NevisWealth.Com"));
    }

    @Test
    @DisplayName("GET /search with similarities")
    void httpSearch_similarities_matches() throws Exception {
        ClientEntity nevisWealth = new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@neviswealth.com",
                "UK"
        );
        ClientEntity otherWealth = new ClientEntity(
                "Alice",
                "Smith",
                "alice.smith@otherwealth.com",
                "UK"
        );
        clientRepository.save(nevisWealth);
        clientRepository.save(otherWealth);

        mockMvc.perform(get("/search")
                        .param("q", "Wealth")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].type", containsInAnyOrder("client", "client")))
                .andExpect(jsonPath("$[*].email",
                        containsInAnyOrder("john.wealth@neviswealth.com", "alice.smith@otherwealth.com")));
    }

    @Test
    @DisplayName("GET /search with blank q should return empty array")
    void httpSearch_blankQuery_returnsEmptyArray() throws Exception {
        mockMvc.perform(get("/search")
                        .param("q", "   ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

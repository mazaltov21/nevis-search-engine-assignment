package org.divorobioff.nevis.assignment.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class SearchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @MockitoBean
    private VectorStore vectorStore;
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

    @Test
    @DisplayName("GET /search?q=address proof should return document results when semantic search matches")
    void httpSearch_addressProof_returnsDocumentResults() throws Exception {
        ClientEntity client = new ClientEntity(
                "Alice",
                "Cooper",
                "alice.cooper@neviswealth.com",
                "UK"
        );
        client = clientRepository.save(client);

        DocumentEntity doc = new DocumentEntity();
        doc.setClient(client);
        doc.setTitle("Monthly payment");
        doc.setContent("Utility bill for January 350 EUR");
        doc.setCreatedAt(LocalDateTime.now());
        doc = documentRepository.save(doc);

        Document vectorDoc = new Document(
                doc.getContent(),
                Map.of(
                        "id", doc.getId().toString(),
                        "clientId", client.getId().toString(),
                        "title", doc.getTitle(),
                        "createdAt", doc.getCreatedAt().toString()
                )
        );

        when(vectorStore.similaritySearch(argThat((SearchRequest req) ->
                "address proof".equals(req.getQuery()))))
                .thenReturn(List.of(vectorDoc));

        mockMvc.perform(get("/search")
                        .param("q", "address proof")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("document"))
                .andExpect(jsonPath("$[0].title").value("Monthly payment"))
                .andExpect(jsonPath("$[0].clientId").value(client.getId().toString()))
                .andExpect(jsonPath("$[0].content",
                        containsString("Utility bill for January 350 EUR")));
    }

    @Test
    @DisplayName("GET /search can return both client and document results together")
    void httpSearch_returnsClientAndDocumentResults() throws Exception {
        ClientEntity client = new ClientEntity(
                "Alice",
                "Nevis",
                "alice.nevis@neviswealth.com",
                "CH"
        );
        client = clientRepository.save(client);

        DocumentEntity doc = new DocumentEntity();
        doc.setClient(client);
        doc.setTitle("KYC information");
        doc.setContent("Proof of funds and utility bill attached");
        doc.setCreatedAt(LocalDateTime.now());
        doc = documentRepository.save(doc);

        Document vectorDoc = new Document(
                doc.getContent(),
                Map.of(
                        "id", doc.getId().toString(),
                        "clientId", client.getId().toString(),
                        "title", doc.getTitle(),
                        "createdAt", doc.getCreatedAt().toString()
                )
        );

        when(vectorStore.similaritySearch(argThat((SearchRequest req) ->
                "Nevis Wealth".equals(req.getQuery()))))
                .thenReturn(List.of(vectorDoc));

        mockMvc.perform(get("/search")
                        .param("q", "Nevis Wealth")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].type", containsInAnyOrder("client", "document")))
                .andExpect(jsonPath("$[?(@.type == 'client')].email").value(hasItem("alice.nevis@neviswealth.com")))
                .andExpect(jsonPath("$[?(@.type == 'document')].title").value(hasItem("KYC information")));
    }
}

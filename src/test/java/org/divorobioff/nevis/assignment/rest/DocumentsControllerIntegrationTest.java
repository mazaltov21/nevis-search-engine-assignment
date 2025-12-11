package org.divorobioff.nevis.assignment.rest;

import org.divorobioff.nevis.assignment.WireMockTestConfig;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.entity.repo.DocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@Import(WireMockTestConfig.class)
public class DocumentsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @AfterEach
    void cleanup() {
        documentRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /documents/summary/{id} returns 'Mocked AI response' from WireMock")
    void summary_endToEnd_usesWireMock() throws Exception {
        ClientEntity client = clientRepository.save(new ClientEntity(
                "Alice", "Cooper", "alice@neviswealth.com", "UK"
        ));
        DocumentEntity doc = new DocumentEntity();
        doc.setClient(client);
        doc.setTitle("Proof of address");
        doc.setContent("Utility bill for January 350 EUR");
        doc.setCreatedAt(LocalDateTime.now());
        doc = documentRepository.save(doc);
        mockMvc.perform(get("/documents/summary/{id}", doc.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("Mocked AI response"));
    }
}

package org.divorobioff.nevis.assignment.service;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class SearchServiceIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SearchServiceApi searchService;
    @MockitoBean
    private VectorStore vectorStore;

    @AfterEach
    void cleanUp() {
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("search('Nevis') should return clients from nevis.com and neviswealth.com")
    void search_nevis_matchesNevisAndNevisWealth() {
        ClientEntity nevisWealth = new ClientEntity(
                "Alice",
                "Smith",
                "alice.smith@neviswealth.com",
                "UK"
        );
        ClientEntity nevis = new ClientEntity(
                "Bob",
                "Nevis",
                "bob@nevis.com",
                "NL"
        );
        ClientEntity other = new ClientEntity(
                "John",
                "Other",
                "john@somethingelse.com",
                "CH"
        );

        clientRepository.save(nevisWealth);
        clientRepository.save(nevis);
        clientRepository.save(other);

        List<SearchResult> results = searchService.search("Nevis");

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(r -> ((SearchResult.ClientResult) r).email())
                .containsExactlyInAnyOrder(
                        "alice.smith@neviswealth.com",
                        "bob@nevis.com"
                );
    }

    @Test
    @DisplayName("search('wealth') should return clients from neviswealth.com")
    void search_wealth_matchesNevisWealth() {
        ClientEntity nevisWealth = new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@neviswealth.com",
                "UK"
        );
        ClientEntity nevis = new ClientEntity(
                "Alice",
                "Nevis",
                "alice@nevis.com",
                "NL"
        );

        clientRepository.save(nevisWealth);
        clientRepository.save(nevis);


        List<SearchResult> results = searchService.search("wealth");

        assertThat(results).hasSize(1);
        SearchResult.ClientResult client = (SearchResult.ClientResult) results.getFirst();
        assertThat(client.email()).isEqualTo("john.wealth@neviswealth.com");
    }

    @Test
    @DisplayName("search normalizes case and punctuation")
    void search_normalizesInput() {
        ClientEntity nevisWealth = new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@NevisWealth.Com",
                "CH"
        );
        clientRepository.save(nevisWealth);

        List<SearchResult> results = searchService.search("  nEvIs   WEALTH!! ");

        assertThat(results).hasSize(1);
        SearchResult.ClientResult client = (SearchResult.ClientResult) results.getFirst();
        assertThat(client.email()).isEqualTo("john.wealth@NevisWealth.Com");
    }

    @Test
    @DisplayName("search handles domains with hyphens and multiple parts")
    void search_handlesHyphensAndMultiPartDomains() {
        ClientEntity hyphenDomain = new ClientEntity(
                "John",
                "Hyphen",
                "john@nevis-wealth-group.co.uk",
                "UK"
        );
        ClientEntity simpleDomain = new ClientEntity(
                "Sam",
                "Simple",
                "sam@neviswealth.com",
                "CH"
        );
        ClientEntity other = new ClientEntity(
                "Other",
                "Guy",
                "other@something.com",
                "NL"
        );

        clientRepository.save(hyphenDomain);
        clientRepository.save(simpleDomain);
        clientRepository.save(other);

        List<SearchResult> byWealth = searchService.search("wealth");

        assertThat(byWealth)
                .extracting(r -> ((SearchResult.ClientResult) r).email())
                .containsExactlyInAnyOrder(
                        "john@nevis-wealth-group.co.uk",
                        "sam@neviswealth.com"
                );

        List<SearchResult> byGroup = searchService.search("group");

        assertThat(byGroup)
                .extracting(r -> ((SearchResult.ClientResult) r).email())
                .containsExactly("john@nevis-wealth-group.co.uk");
    }

    @Test
    @DisplayName("search matches substring from the middle of the domain")
    void search_matchesMiddleSubstring() {
        ClientEntity nevisWealth = new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@neviswealth.com",
                "CH"
        );
        clientRepository.save(nevisWealth);

        List<SearchResult> results = searchService.search("viswea");

        assertThat(results).hasSize(1);
        SearchResult.ClientResult client = (SearchResult.ClientResult) results.getFirst();
        assertThat(client.email()).isEqualTo("john.wealth@neviswealth.com");
    }

    @Test
    @DisplayName("search query that normalizes to empty returns empty list")
    void search_queryNormalizesToEmpty_returnsEmpty() {
        clientRepository.save(new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@neviswealth.com",
                "CH"
        ));
        List<SearchResult> results = searchService.search("!!! $$$ ###");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("search returns empty when there is no match")
    void search_noMatch_returnsEmpty() {
        clientRepository.save(new ClientEntity(
                "John",
                "Wealth",
                "john.wealth@neviswealth.com",
                "CH"
        ));

        List<SearchResult> results = searchService.search("totallydifferentcompany");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("search with blank query returns empty list")
    void search_blank_returnsEmpty() {
        assertThat(searchService.search("   ")).isEmpty();
        assertThat(searchService.search(null)).isEmpty();
    }
}

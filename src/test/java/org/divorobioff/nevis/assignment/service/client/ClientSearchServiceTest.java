package org.divorobioff.nevis.assignment.service.client;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientSearchServiceTest {

    @Mock
    private ClientRepository clientRepository;
    private ClientSearchService clientSearchService;

    @BeforeEach
    void setUp() {
        clientSearchService = new ClientSearchService(clientRepository);
    }

    @Test
    @DisplayName("searchClients(null) should return empty list and not call repository")
    void searchClients_nullQuery_returnsEmpty() {
        List<SearchResult.ClientResult> results = clientSearchService.searchClients(null);

        assertThat(results).isEmpty();
        verifyNoInteractions(clientRepository);
    }

    @Test
    @DisplayName("searchClients with blank query should return empty list and not call repository")
    void searchClients_blankQuery_returnsEmpty() {
        List<SearchResult.ClientResult> results = clientSearchService.searchClients("   ");

        assertThat(results).isEmpty();
        verifyNoInteractions(clientRepository);
    }

    @Test
    @DisplayName("searchClients with valid query normalizes input and maps repository results")
    void searchClients_validQuery_delegatesAndMaps() {
        ClientEntity client1 = new ClientEntity(
                "Alice",
                "Nevis",
                "alice@neviswealth.com",
                "CH"
        );
        client1.setId(UUID.randomUUID());

        ClientEntity client2 = new ClientEntity(
                "Bob",
                "Smith",
                "bob@nevis.com",
                "UK"
        );
        client2.setId(UUID.randomUUID());

        when(clientRepository.findByCompany("neviswealth"))
                .thenReturn(List.of(client1, client2));

        List<SearchResult.ClientResult> results =
                clientSearchService.searchClients("  Nevis Wealth  ");

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(clientRepository, times(1)).findByCompany(queryCaptor.capture());
        assertThat(queryCaptor.getValue()).isEqualTo("neviswealth");

        assertThat(results).hasSize(2);

        assertThat(results)
                .extracting(SearchResult.ClientResult::email)
                .containsExactlyInAnyOrder(
                        "alice@neviswealth.com",
                        "bob@nevis.com"
                );

        assertThat(results)
                .extracting(SearchResult.ClientResult::firstName)
                .containsExactlyInAnyOrder("Alice", "Bob");

        assertThat(results)
                .extracting(SearchResult.ClientResult::countryOfResidence)
                .containsExactlyInAnyOrder("CH", "UK");
    }

    @Test
    @DisplayName("searchClients with query that normalizes to empty returns empty list")
    void searchClients_queryNormalizesToEmpty_returnsEmpty() {
        List<SearchResult.ClientResult> results = clientSearchService.searchClients("!!!");

        assertThat(results).isEmpty();
        verifyNoInteractions(clientRepository);
    }
}

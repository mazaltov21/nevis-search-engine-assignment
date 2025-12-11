package org.divorobioff.nevis.assignment.service.client;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.service.client.api.ClientSearchServiceApi;
import org.divorobioff.nevis.assignment.utility.CompanyQueryNormalizer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientSearchService implements ClientSearchServiceApi {

    private final ClientRepository clientRepository;

    public ClientSearchService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<SearchResult.ClientResult> searchClients(String query) {
        String normalized = CompanyQueryNormalizer.normalize(query);
        if (normalized.isBlank()) {
            return List.of();
        }
        List<SearchResult.ClientResult> results = new ArrayList<>();
        clientRepository.findByCompany(normalized).forEach(client ->
                results.add(new SearchResult.ClientResult(
                        client.getId().toString(),
                        client.getFirstName(),
                        client.getLastName(),
                        client.getEmail(),
                        client.getCountryOfResidence()
                ))
        );
        return results;
    }
}

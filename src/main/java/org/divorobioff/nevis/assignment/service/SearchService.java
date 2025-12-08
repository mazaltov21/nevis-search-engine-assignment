package org.divorobioff.nevis.assignment.service;

import org.divorobioff.nevis.assignment.dto.response.SearchResult;
import org.divorobioff.nevis.assignment.dto.response.SearchResultType;
import org.divorobioff.nevis.assignment.entity.repo.ClientRepository;
import org.divorobioff.nevis.assignment.service.api.SearchServiceApi;
import org.divorobioff.nevis.assignment.utility.CompanyQueryNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchService implements SearchServiceApi {

    private final ClientRepository clientRepository;

    public SearchService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<SearchResult> search(String q) {
        String normalized = CompanyQueryNormalizer.normalize(q);
        if (normalized.isBlank()) {
            return List.of();
        }
        List<SearchResult> results = new ArrayList<>();
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

package org.divorobioff.nevis.assignment.entity.repo;

import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    @Query(value = """
        SELECT c.* FROM clients c
        WHERE c.email_domain ILIKE CONCAT('%', :query, '%')
        ORDER BY similarity(c.email_domain, :query) DESC
        LIMIT 50
        """, nativeQuery = true)
    List<ClientEntity> findByCompany(@Param("query") String query);

    boolean existsByEmail(String email);
}

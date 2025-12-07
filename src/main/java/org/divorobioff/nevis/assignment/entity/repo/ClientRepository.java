package org.divorobioff.nevis.assignment.entity.repo;

import org.divorobioff.nevis.assignment.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}

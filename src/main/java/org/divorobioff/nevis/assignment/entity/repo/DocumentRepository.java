package org.divorobioff.nevis.assignment.entity.repo;

import org.divorobioff.nevis.assignment.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {

    List<DocumentEntity> findByClientId(UUID clientId);
}

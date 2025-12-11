package org.divorobioff.nevis.assignment.service.document.api;

import org.divorobioff.nevis.assignment.entity.DocumentEntity;

public interface DocumentIndexServiceApi {

    void addVectorIndex(DocumentEntity document);
}

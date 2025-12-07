package org.divorobioff.nevis.assignment.mapper;

import org.divorobioff.nevis.assignment.dto.request.DocumentRequestDto;
import org.divorobioff.nevis.assignment.dto.response.DocumentResponseDto;
import org.divorobioff.nevis.assignment.entity.DocumentEntity;

public final class DocumentMapper {

    private DocumentMapper() {}

    public static DocumentEntity toEntity(DocumentRequestDto dto) {
        DocumentEntity entity = new DocumentEntity();
        entity.setTitle(dto.title());
        entity.setContent(dto.content());
        return entity;
    }

    public static DocumentResponseDto toDto(DocumentEntity entity) {
        return new DocumentResponseDto(
                entity.getId().toString(),
                entity.getClient().getId().toString(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}

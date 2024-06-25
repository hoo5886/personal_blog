package com.example.personal_blog.model;

import com.example.personal_blog.entity.ContentPath;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ContentPathDto(
    Long contentPathId,
    Long articleId,
    String path,
    LocalDateTime createdAt
) {
        public ContentPathDto {
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
            }
        }

        public ContentPathDto withArticleId(Long articleId) {
            return new ContentPathDto(contentPathId, articleId, path, createdAt);
        }

        public static ContentPathDto from(ContentPath contentPath) {
            return ContentPathDto.builder()
                .contentPathId(contentPath.getContentPathId())
                .articleId(contentPath.getArticle().getArticleId())
                .path(contentPath.getContentPath())
                .createdAt(contentPath.getCreatedAt())
                .build();

        }

        public static ContentPath to(ContentPathDto dto) {
            return ContentPath.builder()
                    .contentPathId(dto.contentPathId())
                    .contentPath(dto.path())
                    .createdAt(dto.createdAt())
                    .build();
        }

}

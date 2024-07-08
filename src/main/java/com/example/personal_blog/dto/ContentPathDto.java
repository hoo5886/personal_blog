package com.example.personal_blog.dto;

import com.example.personal_blog.entity.ContentPath;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ContentPathDto(
    Long contentPathId,
    ArticleDto articleDto,
    String path,
    LocalDateTime createdAt
) {
        public ContentPathDto {
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
            }
        }

        public static ContentPathDto from(ContentPath contentPath) {
            return ContentPathDto.builder()
                .contentPathId(contentPath.getContentPathId())
                .articleDto(ArticleDto.from(contentPath.getArticle()))
                .path(contentPath.getContentPath())
                .build();

        }

        public static ContentPath to(ContentPathDto dto) {
            return ContentPath.builder()
                    .contentPathId(dto.contentPathId())
                    .contentPath(dto.path())
                    .build();
        }
}

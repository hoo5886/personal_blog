package com.example.personal_blog.dto;

import com.example.personal_blog.entity.ContentPath;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record ContentPathDto(
    Long contentPathId,
    String path,
    Long articleId
) {
        public static ContentPathDto from(ContentPath contentPath) {
            return ContentPathDto.builder()
                .contentPathId(contentPath.getContentPathId())
                .path(contentPath.getContentPath())
                .articleId(contentPath.getArticle().getArticleId())
                .build();

        }

        public static ContentPath to(ContentPathDto dto) {
            return ContentPath.builder()
                    .contentPathId(dto.contentPathId())
                    .contentPath(dto.path())
                    .build();
        }
}

package com.example.personal_blog.model;

import com.example.personal_blog.entity.Article;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record ArticleDto(
    Long articleId,
    String title,
    String content,
    long hits,
    int likes,
    boolean isDeleted,
    Set<ContentPathDto> contentPaths,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public ArticleDto {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (contentPaths == null) {
            contentPaths = new HashSet<>();
        }
    }

    //
    public static ArticleDto from(Article article) {
        return ArticleDto.builder()
            .articleId(article.getArticleId())
            .title(article.getTitle())
            .content(article.getContent())
            .hits(article.getHits())
            .likes(article.getLikes())
            .contentPaths(article.getContentPaths().stream()
                .map(ContentPathDto::from)
                .collect(Collectors.toSet()))
            .isDeleted(article.isDeleted())
            .createdAt(article.getCreatedAt())
            .updatedAt(article.getUpdatedAt())
            .build();
    }

    public static Article to(ArticleDto dto) {
        return Article.builder()
                .articleId(dto.articleId())
                .title(dto.title())
                .content(dto.content())
                .hits(dto.hits())
                .likes(dto.likes())
                .contentPaths(dto.contentPaths().stream()
                    .map(ContentPathDto::to)
                    .collect(Collectors.toSet()))
                .isDeleted(dto.isDeleted())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .build();
    }
}

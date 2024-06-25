package com.example.personal_blog.model;

import com.example.personal_blog.entity.Article;
import java.time.LocalDateTime;
import java.util.Set;
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
    }
    public ArticleDto withIsDeleted(boolean isDeleted) {
        return new ArticleDto(articleId, title, content, hits, likes, isDeleted, contentPaths, createdAt, updatedAt);
    }

    //
    public static ArticleDto from(Article article) {
        return ArticleDto.builder()
            .articleId(article.getArticleId())
            .title(article.getTitle())
            .content(article.getContent())
            .hits(article.getHits())
            .likes(article.getLikes())
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
                .likes(dto.likes())
                .hits(dto.hits())
                .isDeleted(dto.isDeleted())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .build();
    }
}

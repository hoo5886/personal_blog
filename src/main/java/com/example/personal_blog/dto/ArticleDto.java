package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.Comment;
import com.example.personal_blog.entity.ContentPath;
import com.example.personal_blog.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder(toBuilder = true)
public record ArticleDto(
    Long articleId,
    String title,
    String content,
    long hits,
    int likes,
    boolean isDeleted,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long userId,
    String loginId,
    String username,
    Set<ContentPathDto> contentPaths,
    List<CommentDto> commentDtos
) {

    public ArticleDto {
        if (contentPaths == null) {
            contentPaths = new HashSet<>();
        }
        if (commentDtos == null) {
            commentDtos = List.of();
        }
    }

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
            .userId(article.getUser().getUserId())
            .loginId(article.getUser().getLoginId())
            .username(article.getUser().getUsername())
            .contentPaths(article.getContentPaths().stream()
                .map(ContentPathDto::from)
                .collect(Collectors.toSet()))
            .commentDtos(article.getComments().stream()
                .map(CommentDto::from)
                .collect(Collectors.toList()))
            .build();
    }

    public static Article to(ArticleDto dto, User user, List<Comment> comments, Set<ContentPath> contentPaths) {
        return Article.builder()
                .articleId(dto.articleId())
                .title(dto.title())
                .content(dto.content())
                .user(user)
                .hits(dto.hits())
                .likes(dto.likes())
                .isDeleted(dto.isDeleted())
                .contentPaths(contentPaths != null ? contentPaths : Set.of())
                .comments(comments != null ? comments : List.of())
                .build();
    }
}

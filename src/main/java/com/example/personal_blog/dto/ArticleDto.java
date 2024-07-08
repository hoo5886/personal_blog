package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Article;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record ArticleDto(
    Long articleId,
    String title,
    String content,
    UserDto userDto,
    long hits,
    int likes,
    boolean isDeleted,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
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
            .userDto(UserDto.from(article.getUser()))
            .hits(article.getHits())
            .likes(article.getLikes())
            .isDeleted(article.isDeleted())
            .createdAt(article.getCreatedAt())
            .updatedAt(article.getUpdatedAt())
            .contentPaths(article.getContentPaths().stream()
                .map(ContentPathDto::from)
                .collect(Collectors.toSet()))
            .commentDtos(article.getComments().stream()
                .map(CommentDto::from)
                .collect(Collectors.toList()))
            .build();
    }

    public static Article to(ArticleDto dto) {
        return Article.builder()
                .articleId(dto.articleId())
                .title(dto.title())
                .content(dto.content())
                .user(UserDto.to(dto.userDto()))
                .hits(dto.hits())
                .likes(dto.likes())
                .isDeleted(dto.isDeleted())
                .contentPaths(dto.contentPaths().stream()
                    .map(ContentPathDto::to)
                    .collect(Collectors.toSet()))
                .comments(dto.commentDtos().stream()
                    .map(CommentDto::to)
                    .collect(Collectors.toList()))
                .build();
    }
}

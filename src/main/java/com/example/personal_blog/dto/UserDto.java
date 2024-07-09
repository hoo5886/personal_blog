package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.Authority;
import com.example.personal_blog.entity.Comment;
import com.example.personal_blog.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonIgnoreProperties({"articleDtos", "commentDtos"})
public record UserDto(
    Long userId,
    String loginId,
    String username,
    String password,
    boolean enabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<ArticleDto> articleDtos,
    List<AuthorityDto> authorityDtos,
    List<CommentDto> commentDtos
) {
    public UserDto {
        if (articleDtos == null) {
            articleDtos = List.of();
        }
        if (authorityDtos == null) {
            authorityDtos = List.of();
        }
        if (commentDtos == null) {
            commentDtos = List.of();
        }
    }

    public static UserDto from(User user) {
        return UserDto.builder()
            .userId(user.getUserId())
            .loginId(user.getLoginId())
            .username(user.getUsername())
            .password(user.getPassword())
            .enabled(user.isEnabled())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .articleDtos(user.getArticles().stream()
                .map(ArticleDto::from)
                .toList())
            .authorityDtos(user.getAuthorities().stream()
                .map(authority -> (Authority) authority)
                .map(AuthorityDto::from)
                .collect(Collectors.toList()))
            .commentDtos(user.getComments().stream()
                .map(CommentDto::from)
                .toList())
            .build();
    }

    public static User to(UserDto dto, List<Article> articles, List<Comment> comments, List<Authority> authorities) {
        return User.builder()
                .userId(dto.userId())
                .loginId(dto.loginId())
                .username(dto.username())
                .password(dto.password())
                .enabled(dto.enabled())
                .articles(articles != null ? articles : List.of())
                .comments(comments != null ? comments : List.of())
                .authorities(authorities != null ? authorities : List.of())
                .build();
    }
}
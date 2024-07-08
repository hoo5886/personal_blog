package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Authority;
import com.example.personal_blog.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
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

    public static User to(UserDto dto) {
        return User.builder()
                .userId(dto.userId())
                .loginId(dto.loginId())
                .username(dto.username())
                .password(dto.password())
                .enabled(dto.enabled())
                .articles(dto.articleDtos().stream()
                    .map(ArticleDto::to)
                    .collect(Collectors.toList()))
                .authorities(dto.authorityDtos().stream()
                    .map(AuthorityDto::to)
                    .collect(Collectors.toList()))
                .comments(dto.commentDtos().stream()
                    .map(CommentDto::to)
                    .collect(Collectors.toList()))
                .build();
    }
}
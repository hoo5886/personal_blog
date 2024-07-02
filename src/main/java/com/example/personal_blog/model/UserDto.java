package com.example.personal_blog.model;

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
    List<ArticleDto> articleDtos,
    List<AuthorityDto> authorityDtos,
    LocalDateTime createdAt) {

    public UserDto {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public static UserDto from(User user) {
        return UserDto.builder()
            .userId(user.getUserId())
            .loginId(user.getLoginId())
            .username(user.getUsername())
            .password(user.getPassword())
            .enabled(user.isEnabled())
            .authorityDtos(user.getAuthorities().stream()
                .map(authority -> (Authority) authority)
                .map(AuthorityDto::from)
                .collect(Collectors.toList()))
            .articleDtos(user.getArticles().stream()
                .map(ArticleDto::from)
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
                .build();
    }
}
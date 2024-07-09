package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Authority;
import com.example.personal_blog.entity.Role;
import com.example.personal_blog.entity.User;
import lombok.Builder;

@Builder
public record AuthorityDto(
    Long authorityId,
    Role role,
    UserDto userDto
) {

    public static AuthorityDto from(Authority authority) {
        return AuthorityDto.builder()
            .authorityId(authority.getAuthorityId())
            .role(authority.getRole())
            .userDto(UserDto.from(authority.getUser()))
            .build();
    }

    public static Authority to(AuthorityDto authorityDto, User user) {
        return Authority.builder()
                .authorityId(authorityDto.authorityId())
                .role(authorityDto.role())
                .user(user)
                .build();
    }
}

package com.example.personal_blog.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SignUpRequest(
    String loginId,
    String password,
    String username,
    LocalDateTime createdAt,
    boolean enabled
) {
    public SignUpRequest {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

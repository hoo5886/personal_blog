package com.example.personal_blog.dto.request;

import lombok.Builder;

@Builder
public record SignInRequest(
    String loginId,
    String password
) {

}

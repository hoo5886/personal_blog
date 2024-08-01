package com.example.personal_blog.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public void blacklistToken(String token, long expiration) {
        // 토큰을 블랙리스트에 추가하고, 만료 시간 설정
        redisTemplate.opsForValue().set(token, "blacklisted", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        // 토큰이 블랙리스트에 있는지 확인
        return redisTemplate.hasKey(token);
    }
}

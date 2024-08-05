package com.example.personal_blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.personal_blog.entity.Authority;
import com.example.personal_blog.entity.Role;
import com.example.personal_blog.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtServiceTest {

    @Value("${spring.token.signing.key}")
    private String jwtSigningKey;

    private JwtService jwtService;
    private User user;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        jwtService.setJwtSigningKey(jwtSigningKey);

        user = User.builder()
            .loginId("loginId")
            .password("12345")
            .build();
    }

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
    }

    @Test
    public void testExtractLoginId() {
        String token = jwtService.generateToken(user);
        assertEquals("testuser", jwtService.extractLoginId(token));
    }

    @Test
    public void testValidateToken() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));
    }
}

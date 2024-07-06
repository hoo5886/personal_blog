package com.example.personal_blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.personal_blog.entity.Authority;
import com.example.personal_blog.entity.Role;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
public class JwtServiceTest {

    @Value("${spring.token.signing.key}")
    private String jwtSigningKey;

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        jwtService.setJwtSigningKey(jwtSigningKey);
        userDetails = User.withUsername("testuser")
            .password("password")
            .authorities(List.of(Authority.builder().role(Role.ROLE_USER).build()))
            .build();
    }

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
    }

    @Test
    public void testExtractLoginId() {
        String token = jwtService.generateToken(userDetails);
        assertEquals("testuser", jwtService.extractLoginId(token));
    }

    @Test
    public void testValidateToken() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
}

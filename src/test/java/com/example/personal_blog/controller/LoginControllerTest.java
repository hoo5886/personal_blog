package com.example.personal_blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.personal_blog.config.test.SecurityTestConfig;
import com.example.personal_blog.dto.request.SignInRequest;
import com.example.personal_blog.dto.request.SignUpRequest;
import com.example.personal_blog.dto.response.JwtAuthenticationResponse;
import com.example.personal_blog.service.AuthenticationService;
import com.example.personal_blog.service.JpaUserDetailsService;
import com.example.personal_blog.service.JwtService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LoginController.class)
@Import(SecurityTestConfig.class)
@ActiveProfiles("test")
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JpaUserDetailsService jpaUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper; // Spring이 주입해주는 ObjectMapper 사용

    private JwtAuthenticationResponse response;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        response = JwtAuthenticationResponse.builder().token("testToken").build();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    @WithMockUser(username = "testUser", password = "password", roles = "USER")
    void testSignIn() throws Exception {
        SignInRequest signInRequest = new SignInRequest("testUser", "password");

        when(authenticationService.signIn(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest))) // ObjectMapper로 JSON 문자열로 변환
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(response.getToken()));
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void testSignInFail() throws Exception {
        SignInRequest signInRequest = new SignInRequest("testUser1", "password");

        when(authenticationService.signIn(any())).thenThrow(new UsernameNotFoundException(
            "아이디 또는 비밀번호가 유효하지 않습니다."
        ));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testSignUp() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .loginId("testUser")
            .username("testUser")
            .password("password")
            .createdAt(null)
            .build();

        // Mocking the authenticationService
        when(authenticationService.signUp(any())).thenReturn(response);

        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isCreated());
    }

}

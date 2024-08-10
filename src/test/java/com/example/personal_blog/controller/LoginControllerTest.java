package com.example.personal_blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.personal_blog.config.test.SecurityTestConfig;
import com.example.personal_blog.dto.request.SignInRequest;
import com.example.personal_blog.dto.request.SignUpRequest;
import com.example.personal_blog.dto.response.JwtAuthenticationResponse;
import com.example.personal_blog.service.AuthenticationService;
import com.example.personal_blog.service.JpaUserDetailsService;
import com.example.personal_blog.service.JwtBlacklistService;
import com.example.personal_blog.service.JwtService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(LoginController.class)
@Import(SecurityTestConfig.class)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JpaUserDetailsService jpaUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtBlacklistService jwtBlacklistService;

    @Autowired
    private ObjectMapper objectMapper; // Spring이 주입해주는 ObjectMapper 사용

    private JwtAuthenticationResponse response;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

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
            .andExpect(jsonPath("$.token").value(response.getToken()))
            .andDo(document("auth-login",
                requestFields(
                    fieldWithPath("loginId").description("로그인 아이디"),
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("token").description("JWT 토큰")
                )
            ));
    }

    @Test
    @DisplayName("잘못된 자격 증명으로 로그인 시 실패 응답을 반환한다")
    void signIn_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        SignInRequest signInRequest = new SignInRequest("testUser1", "wrongPassword");
        String errorMessage = "아이디 또는 비밀번호가 유효하지 않습니다.";

        when(authenticationService.signIn(any(SignInRequest.class)))
            .thenThrow(new UsernameNotFoundException(errorMessage));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"))
            .andExpect(jsonPath("$.message").value(errorMessage))
            .andDo(document("auth-login-fail",
                requestFields(
                    fieldWithPath("loginId").description("로그인 아이디"),
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("error").description("에러 유형"),
                    fieldWithPath("message").description("에러 메시지")
                )
            )
        );
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
            .andExpect(status().isCreated())
            .andDo(document("auth-signup",
                responseFields(
                    fieldWithPath("token").description("JWT 토큰")
                )
            ));
    }

    @Test
    @DisplayName("로그아웃")
    @WithMockUser(username = "testUser", password = "password", roles = "USER")
    void testSignOut() throws Exception {
        String validToken = "valid.jwt.token";

        doNothing().when(jwtBlacklistService).blacklistToken(validToken, 1L);
        when(jwtService.isTokenExpired(anyString())).thenReturn(true);
        when(jwtService.extractExpiration(anyString())).thenReturn(new Date(System.currentTimeMillis() + 1000000));

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""))
            .andDo(document("auth-logout",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer JWT token")
                )
            ));
    }
}

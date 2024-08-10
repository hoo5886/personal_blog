package com.example.personal_blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.personal_blog.dto.ArticleDto;
import com.example.personal_blog.dto.CommentDto;
import com.example.personal_blog.dto.UserDto;
import com.example.personal_blog.service.CommentService;
import com.example.personal_blog.service.JpaUserDetailsService;
import com.example.personal_blog.service.JwtService;
import com.example.personal_blog.testconfig.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)
@WebMvcTest(CommentController.class)
@WithMockUser(username = "testUser", roles = "USER")
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JpaUserDetailsService jpaUserDetailsService;

    @MockBean
    private JwtService jwtService;

    ArticleDto articleDto;
    CommentDto commentDto;
    UserDto userDto;

    private static final String SECRET_KEY = "ADqweqw@#Jjjhu&880=svmeriuhvfg;ph!@#+_23grteg89789090-fwrhvbhEER^^sadfhbvuohow";

    private String createToken() {
        return Jwts.builder()
            .setSubject("testUser")
            .claim("userId", "1")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간
            .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }

    private RequestPostProcessor bearerToken() {
        return request -> {
            request.addHeader("Authorization", "Bearer " + createToken());
            return request;
        };
    }

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        var now = LocalDateTime.now();

        userDto = UserDto.builder()
            .userId(1L)
            .enabled(true)
            .username("이름")
            .createdAt(now)
            .loginId("로그인아이디")
            .password("비밀번호")
            .build();

        articleDto = ArticleDto.builder()
            .articleId(1L)
            .title("제목")
            .content("내용")
            .hits(0)
            .likes(0)
            .isDeleted(false)
            .createdAt(now)
            .loginId(userDto.loginId())
            .username(userDto.username())
            .build();

        commentDto = CommentDto.builder()
            .commentId(1L)
            .articleId(1L)
            .nickname("닉네임")
            .comment("댓글")
            .userId(userDto.userId())
            .articleId(articleDto.articleId())
            .createdAt(now)
            .build();

        when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);
        when(jwtService.extractLoginId(anyString())).thenReturn("testUser");
    }

    @Test
    @DisplayName("댓글 생성")
    void createComment() throws Exception {
        // given
        // when
        when(commentService.createComment(anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        // then
        mockMvc.perform(post("/articles/{articleId}/comments", 1L)
            .with(bearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(commentDto)))
            .andExpect(status().isCreated()) //에러지점
            .andExpect(jsonPath("$.commentId").value(commentDto.commentId()))
            .andExpect(jsonPath("$.nickname").value(commentDto.nickname()))
            .andExpect(jsonPath("$.comment").value(commentDto.comment()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andExpect(jsonPath("$.userId").value(commentDto.userId()))
            .andExpect(jsonPath("$.articleId").value(commentDto.articleId()))
            .andDo(print())
            .andDo(document("comment-create",
                responseFields(
                    fieldWithPath("commentId").description("댓글 ID"),
                    fieldWithPath("nickname").description("닉네임"),
                    fieldWithPath("comment").description("댓글 내용"),
                    fieldWithPath("createdAt").description("생성일시"),
                    fieldWithPath("updatedAt").description("수정일시"),
                    fieldWithPath("userId").description("유저 ID"),
                    fieldWithPath("articleId").description("게시글 ID")
                )
            ));
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        // given
        CommentDto updatedComment = CommentDto.builder()
            .commentId(1L)
            .articleId(1L)
            .nickname("수정된 닉네임")
            .comment("수정된 댓글")
            .userId(userDto.userId())
            .createdAt(commentDto.createdAt())
            .build();

        // when
        when(commentService.updateComment(anyLong(), any(CommentDto.class), anyLong())).thenReturn(updatedComment);

        // then
        mockMvc.perform(put("/articles/{articleId}/comments/{commentId}", 1L, 1L)
            .with(bearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updatedComment)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.commentId").value(updatedComment.commentId()))
            .andExpect(jsonPath("$.nickname").value(updatedComment.nickname()))
            .andExpect(jsonPath("$.comment").value(updatedComment.comment()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andExpect(jsonPath("$.userId").value(updatedComment.userId()))
            .andExpect(jsonPath("$.articleId").value(updatedComment.articleId()))
            .andDo(print())
            .andDo(document("comment-update",
                responseFields(
                    fieldWithPath("commentId").description("댓글 ID"),
                    fieldWithPath("nickname").description("닉네임"),
                    fieldWithPath("comment").description("댓글 내용"),
                    fieldWithPath("createdAt").description("생성일시"),
                    fieldWithPath("updatedAt").description("수정일시"),
                    fieldWithPath("userId").description("유저 ID"),
                    fieldWithPath("articleId").description("게시글 ID")
                )
            )
        );
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(delete("/articles/{articleId}/comments/{commentId}", 1L, 1L)
            .with(bearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(commentDto)))
            .andExpect(status().isNoContent())
            .andDo(print())
            .andDo(document("comment-delete"));
    }
}

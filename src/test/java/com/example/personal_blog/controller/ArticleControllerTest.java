package com.example.personal_blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.personal_blog.dto.ArticleDto;
import com.example.personal_blog.dto.CommentDto;
import com.example.personal_blog.dto.ContentPathDto;
import com.example.personal_blog.dto.UserDto;
import com.example.personal_blog.service.ArticleService;
import com.example.personal_blog.service.ContentPathService;
import com.example.personal_blog.service.JpaUserDetailsService;
import com.example.personal_blog.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/*
* If you want to focus only on the web layer and not start a complete ApplicationContext,
* consider using @WebMvcTest instead.
* */
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ArticleController.class)
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private ContentPathService contentPathService;

    @MockBean
    private JpaUserDetailsService jpaUserDetailsService;

    @MockBean
        private JwtService jwtService;

    ArticleDto articleDto;

    CommentDto commentDto;

    UserDto userDto;

    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        var contentPathDto1 = ContentPathDto.builder()
            .contentPathId(1L)
            .articleId(1L)
            .path("/resources/testImages/image1.png")
            .build();

        var contentPathDto2 =ContentPathDto.builder()
            .contentPathId(2L)
            .articleId(1L)
            .path("/resources/testImages/image2.png")
            .build();

        Set<ContentPathDto> contentPaths = new HashSet<>();
        contentPaths.add(contentPathDto1);
        contentPaths.add(contentPathDto2);

        commentDto = CommentDto.builder()
            .commentId(1L)
            .userId(1L)
            .articleId(1L)
            .nickname("nickname")
            .comment("comment")
            .createdAt(now)
            .updatedAt(now)
            .build();

        articleDto = ArticleDto.builder()
            .articleId(1L)
            .title("title")
            .content("content")
            .hits(0)
            .likes(0)
            .userId(1L)
            .loginId("loginId")
            .username("username")
            .commentDtos(List.of(commentDto))
            .contentPaths(contentPaths)
            .isDeleted(false)
            .createdAt(now)
            .updatedAt(now)
            .build();

        userDto = UserDto.builder()
            .userId(1L)
            .loginId("loginId")
            .username("username")
            .password("password")
            .enabled(true)
            .articleDtos(List.of(articleDto))
            .authorityDtos(List.of())
            .commentDtos(List.of(commentDto))
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    @Test
    void hello() throws Exception{
        this.mockMvc.perform(get("/hello").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello World!"))
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString().equals("Hello World!");
    }

    @Test
    @DisplayName("게시글 저장")
    void write() throws Exception {
        String dtoJson = mapper.writeValueAsString(articleDto);

        MockMultipartFile jsonFile = new MockMultipartFile("article", "", "application/json", dtoJson.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("files", "image.jpg", "image/jpeg", "image content".getBytes());

        when(articleService.write(any(ArticleDto.class), any(MultipartFile[].class))).thenReturn(articleDto);
        this.mockMvc.perform(
            multipart("/write")
                .file(jsonFile)
                .file(imageFile)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.articleId").value(1L))
            .andExpect(jsonPath("$.title").value("title"))
            .andExpect(jsonPath("$.content").value("content"))
            .andDo(document("write",
                responseFields(
                    fieldWithPath("articleId").description("Article ID"),
                    fieldWithPath("title").description("Article Title"),
                    fieldWithPath("content").description("Article Content"),
                    fieldWithPath("hits").description("Article Hits"),
                    fieldWithPath("likes").description("Article Likes"),
                    fieldWithPath("isDeleted").description("Article isDeleted"),
                    fieldWithPath("createdAt").description("Article Created At"),
                    fieldWithPath("updatedAt").description("Article Updated At"),
                    fieldWithPath("userId").description("User ID"),
                    fieldWithPath("loginId").description("User Login ID"),
                    fieldWithPath("username").description("User Name"),
                    subsectionWithPath("contentPaths").ignored(),
                    subsectionWithPath("commentDtos").ignored()
                )
            )
        );
    }

    @Test
    @DisplayName("게시글 리스트 조회 (null)")
    void find() throws Exception {
        this.mockMvc.perform(get("/articles")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("index",
                responseFields(
                    subsectionWithPath("[]").description("List of articles")
                )
            )
        );
    }

    @Test
    @DisplayName("게시글 리스트 조회")
    void findList() throws Exception {

        List<ArticleDto> dtoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ArticleDto dto = ArticleDto.builder()
                .articleId((long) i)
                .title("title" + i)
                .content("content" + i)
                .hits(0)
                .likes(0)
                .isDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

            dtoList.add(dto);
        }
        given(articleService.getArticleList()).willReturn(dtoList);

        this.mockMvc.perform(get("/articles")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].articleId").value(0L))
            .andExpect(jsonPath("$[0].title").value("title0"))
            .andExpect(jsonPath("$[0].content").value("content0"))
            .andExpect(jsonPath("$[0].hits").value(0))
            .andExpect(jsonPath("$[0].likes").value(0))
            .andExpect(jsonPath("$[0].isDeleted").value(false))
            .andExpect(jsonPath("$[0].createdAt").exists())
            .andExpect(jsonPath("$[0].updatedAt").exists())
            .andExpect(jsonPath("$[1].articleId").value(1L))
            .andExpect(jsonPath("$[1].title").value("title1"))
            .andExpect(jsonPath("$[1].content").value("content1"))
            .andExpect(jsonPath("$[1].hits").value(0))
            .andExpect(jsonPath("$[1].likes").value(0))
            .andExpect(jsonPath("$[1].isDeleted").value(false))
            .andExpect(jsonPath("$[1].createdAt").exists())
            .andExpect(jsonPath("$[1].updatedAt").exists())
            .andDo(print())
            .andDo(document("articles",
                responseFields(
                    fieldWithPath("[].articleId").description("Article ID"),
                    fieldWithPath("[].title").description("Article Title"),
                    fieldWithPath("[].content").description("Article Content"),
                    fieldWithPath("[].hits").description("Article Hits"),
                    fieldWithPath("[].likes").description("Article Likes"),
                    fieldWithPath("[].isDeleted").description("Article isDeleted"),
                    fieldWithPath("[].createdAt").description("Article Created At"),
                    fieldWithPath("[].updatedAt").description("Article Updated At"),
                    fieldWithPath("[].userId").ignored(),
                    fieldWithPath("[].loginId").ignored(),
                    fieldWithPath("[].username").ignored(),
                    fieldWithPath("[].contentPaths").ignored(),
                    fieldWithPath("[].commentDtos").ignored()
                )
            )
        );
    }

    @Test
    @DisplayName("게시글 조회")
    void read() throws Exception {
        given(articleService.readArticle(1L)).willReturn(articleDto);

        this.mockMvc.perform(get("/articles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.articleId").value(1L))
            .andExpect(jsonPath("$.title").value("title"))
            .andExpect(jsonPath("$.content").value("content"))
            .andExpect(jsonPath("$.hits").value(0))
            .andExpect(jsonPath("$.likes").value(0))
            .andExpect(jsonPath("$.isDeleted").value(false))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists())
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.loginId").value("loginId"))
            .andExpect(jsonPath("$.username").value("username"))
            .andExpect(jsonPath("$.commentDtos[0].commentId").value(1L))
            .andExpect(jsonPath("$.commentDtos[0].nickname").value("nickname"))
            .andExpect(jsonPath("$.commentDtos[0].comment").value("comment"))
            .andExpect(jsonPath("$.commentDtos[0].userId").value(1L))
            .andExpect(jsonPath("$.commentDtos[0].articleId").value(1L))
            .andExpect(jsonPath("$.contentPaths[0].contentPathId").value(1L))
            .andExpect(jsonPath("$.contentPaths[0].path").value("/resources/testImages/image1.png"))
            .andExpect(jsonPath("$.contentPaths[0].articleId").value(1L))
            .andExpect(jsonPath("$.contentPaths[1].contentPathId").value(2L))
            .andExpect(jsonPath("$.contentPaths[1].path").value("/resources/testImages/image2.png"))
            .andExpect(jsonPath("$.contentPaths[1].articleId").value(1L))
            .andDo(print())
            .andDo(document("/articles/{articleId}",
                responseFields(
                    fieldWithPath("articleId").description("Article ID"),
                    fieldWithPath("title").description("Article Title"),
                    fieldWithPath("content").description("Article Content"),
                    fieldWithPath("hits").description("Article Hits"),
                    fieldWithPath("likes").description("Article Likes"),
                    fieldWithPath("isDeleted").description("Article isDeleted"),
                    fieldWithPath("createdAt").description("Article Created At"),
                    fieldWithPath("updatedAt").description("Article Updated At"),
                    fieldWithPath("userId").description("유저 아이디"),
                    fieldWithPath("loginId").description("로그인 아이디"),
                    fieldWithPath("username").description("유저 이름"),
                    fieldWithPath("commentDtos[0].commentId").description("댓글 아이디"),
                    fieldWithPath("commentDtos[0].nickname").description("댓글 작성자"),
                    fieldWithPath("commentDtos[0].comment").description("댓글 내용"),
                    fieldWithPath("commentDtos[0].userId").description("댓글 작성자 아이디"),
                    fieldWithPath("commentDtos[0].articleId").description("댓글이 달린 게시글 아이디"),
                    fieldWithPath("commentDtos[0].createdAt").description("댓글 생성일"),
                    fieldWithPath("commentDtos[0].updatedAt").description("댓글 수정일"),
                    fieldWithPath("contentPaths[0].contentPathId").description("이미지 아이디"),
                    fieldWithPath("contentPaths[0].path").description("이미지 경로"),
                    fieldWithPath("contentPaths[0].articleId").description("게시글 아이디"),
                    fieldWithPath("contentPaths[1].contentPathId").description("이미지 아이디"),
                    fieldWithPath("contentPaths[1].path").description("이미지 경로"),
                    fieldWithPath("contentPaths[1].articleId").description("게시글 아이디")
                )
            )
        );
    }

    @Test
    @DisplayName("좋아요 추가")
    void like() throws Exception {
        willDoNothing().given(articleService).addLike(1L);

        this.mockMvc.perform(put("/articles/1/like")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Likes!"))
            .andDo(document("/articles/{id}/like"));
    }

    @Test
    @DisplayName("좋아요 취소")
    void hate() throws Exception {
        willDoNothing().given(articleService).cancelLike(1L);

        this.mockMvc.perform(put("/articles/1/cancel-like")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Likes canceled"))
            .andDo(document("/articles/{id}/cancel-like"));
    }

    @Test
    public void updateArticleTest() throws Exception {
        String dtoJson = mapper.writeValueAsString(articleDto);


        final String fileName = "testImage1";
        final String fileExtension = ".png";
        final String contentPath = "src/test/resources/testImages/" + fileName + fileExtension;

        FileInputStream fis = new FileInputStream(contentPath);

        when(articleService.readArticle(any(Long.class))).thenReturn(articleDto);
        when(articleService.update(any(Long.class), any(), any())).thenReturn("Updated");

        MockMultipartFile file = new MockMultipartFile("files", fileName + fileExtension, "image/png", fis);

        MockMultipartFile jsonFile = new MockMultipartFile("article", "",
            "application/json", "{ \"articleId\": 1, \"title\": \"title\", \"content\": \"content\" }".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/articles/1/update")
                .file(file)
                .file(jsonFile)
                .contentType("multipart/form-data"))
            .andExpect(status().isOk());
    }
}

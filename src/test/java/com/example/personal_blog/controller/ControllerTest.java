package com.example.personal_blog;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.personal_blog.controller.ArticleRestController;
import com.example.personal_blog.model.ArticleDto;
import com.example.personal_blog.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/*
* If you want to focus only on the web layer and not start a complete ApplicationContext,
* consider using @WebMvcTest instead.
* */
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ArticleRestController.class)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private ContentPathService contentPathService;

    ArticleDto articleDto;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        var contentPathDto1 = ContentPathDto.builder()
            .contentPathId(1L)
            .articleDto(articleDto)
            .path("/resources/testImages/image1.png")
            .build();

        var contentPathDto2 =ContentPathDto.builder()
            .contentPathId(2L)
            .articleDto(articleDto)
            .path("/resources/testImages/image2.png")
            .build();

        Set<ContentPathDto> contentPaths = new HashSet<>();
        contentPaths.add(contentPathDto1);
        contentPaths.add(contentPathDto2);

        articleDto = ArticleDto.builder()
            .articleId(1L)
            .title("title")
            .content("content")
            .hits(0)
            .likes(0)
            .contentPaths(contentPaths)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
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
    @DisplayName("게시글 작성")
    void write() throws Exception {
        String dtoJson = mapper.writeValueAsString(articleDto);

        this.mockMvc.perform(post("/write")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(dtoJson))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(dtoJson))
            .andDo(document("write",
                responseFields(
                    subsectionWithPath("id").description("Article ID"),
                    subsectionWithPath("title").description("Article Title"),
                    subsectionWithPath("content").description("Article Content"),
                    subsectionWithPath("hits").description("Article Hits"),
                    subsectionWithPath("likes").description("Article Likes"),
                    subsectionWithPath("isDeleted").description("Article isDeleted"),
                    subsectionWithPath("createdAt").description("Article Created At"),
                    subsectionWithPath("updatedAt").description("Article Updated At")
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
        for (int i = 0; i < 3; i++) {
            ArticleDto dto = ArticleDto.builder()
                .articleId((long) i)
                .title("title" + i)
                .content("content" + i)
                .hits(0)
                .likes(0)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

            dtoList.add(dto);
        }
        given(articleService.getArticleList()).willReturn(dtoList);

        this.mockMvc.perform(get("/articles")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(0L))
            .andExpect(jsonPath("$[0].title").value("title0"))
            .andExpect(jsonPath("$[0].content").value("content0"))
            .andExpect(jsonPath("$[1].id").value(1L))
            .andExpect(jsonPath("$[1].title").value("title1"))
            .andExpect(jsonPath("$[1].content").value("content1"))
            .andExpect(jsonPath("$[2].id").value(2L))
            .andExpect(jsonPath("$[2].title").value("title2"))
            .andExpect(jsonPath("$[2].content").value("content2"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("/articles",
                responseFields(
                    subsectionWithPath("[].id").description("Id of the one article"),
                    subsectionWithPath("[].title").description("title of the one article"),
                    subsectionWithPath("[].content").description("content of the one article"),
                    subsectionWithPath("[].hits").description("hits of the one article"),
                    subsectionWithPath("[].likes").description("likes of the one article"),
                    subsectionWithPath("[].isDeleted").description("deleted of the one article"),
                    subsectionWithPath("[].createdAt").description("createdAt of the one article"),
                    subsectionWithPath("[].updatedAt").description("updatedAt of the one article")
                )
            )
        );
    }

    @Test
    @DisplayName("게시글 조회")
    void read() throws Exception {
        given(articleService.read(1L)).willReturn(articleDto);

        this.mockMvc.perform(get("/articles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.articleId").value(1L))
            .andExpect(jsonPath("$.title").value("title"))
            .andExpect(jsonPath("$.content").value("content"))
            .andExpect(jsonPath("$.contentPaths[0].contentPathId").value(1L))
            .andExpect(jsonPath("$.contentPaths[0].path").value("/resources/testImages/image1.png"))
            .andExpect(jsonPath("$.contentPaths[1].contentPathId").value(2L))
            .andExpect(jsonPath("$.contentPaths[1].path").value("/resources/testImages/image2.png"))
            .andDo(print())
            .andDo(document("/article",
                responseFields(
                    subsectionWithPath("articleId").description("Id of the article"),
                    subsectionWithPath("title").description("Title of the article"),
                    subsectionWithPath("content").description("Content of the article"),
                    subsectionWithPath("contentPaths").description("List of content paths"),
                    subsectionWithPath("contentPaths[].contentPathId").description("Id of the content path"),
                    subsectionWithPath("contentPaths[].path").description("Path of the content"),
                    subsectionWithPath("hits").description("Hits of the article"),
                    subsectionWithPath("likes").description("Likes of the article"),
                    subsectionWithPath("isDeleted").description("Deleted status of the article"),
                    subsectionWithPath("createdAt").description("Creation time of the article"),
                    subsectionWithPath("updatedAt").description("Update time of the article")
                )
            ));
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

        this.mockMvc.perform(put("/articles/{id}/cancel-like")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Likes cancel"))
            .andDo(document("/articles/{id}/cancel-like"));
    }

    @Test
    public void updateArticleTest() throws Exception {


        final String fileName = "testImage1";
        final String fileExtension = ".png";
        final String contentPath = "src/test/resources/testImages/" + fileName + fileExtension;
        FileInputStream fis = new FileInputStream(contentPath);

        when(articleService.read(any(Long.class))).thenReturn(articleDto);
        when(articleService.update(any(ArticleDto.class), any(Long.class))).thenReturn("Updated");

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

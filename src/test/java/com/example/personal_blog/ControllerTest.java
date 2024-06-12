package com.example.personal_blog;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.personal_blog.controller.ArticleRestController;
import com.example.personal_blog.model.dto.ArticleDto;
import com.example.personal_blog.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
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
    private ArticleService service;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
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
    void write() throws Exception {
        ArticleDto dto = new ArticleDto();
        dto.setId(1L);
        dto.setTitle("title");
        dto.setContent("content");
        dto.setHits(0);
        dto.setLikes(0);
        dto.setDeleted(false);
        dto.setCreatedAt(LocalDateTime.now());
        String dtoJson = mapper.writeValueAsString(dto);

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
                    subsectionWithPath("deleted").description("Article isDeleted"),
                    subsectionWithPath("createdAt").description("Article Created At"),
                    subsectionWithPath("updatedAt").description("Article Updated At")
                )
            )
        );
    }

    @Test
    void find() throws Exception {
        this.mockMvc.perform(get("/list")
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
}

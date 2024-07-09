package com.example.personal_blog.controller;

import com.example.personal_blog.dto.ArticleDto;
import com.example.personal_blog.dto.CommentDto;
import com.example.personal_blog.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(CommentController.class)
public class CommentController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    ArticleDto articleDto;

    CommentDto commentDto;

}

package com.example.personal_blog.controller;

import com.example.personal_blog.model.dto.ArticleDto;
import com.example.personal_blog.service.ArticleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleRestController {

    private final ArticleService service;

    @GetMapping("/hello")
    public String home() {
        return "Hello World!";
    }

    @GetMapping("/list")
    public ResponseEntity<List<ArticleDto>> getArticleList() {
        return new ResponseEntity<>(service.getArticleList(), HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateArticle(@RequestBody ArticleDto dto) {
        service.update(dto);
        return new ResponseEntity<>("글 수정 완료", HttpStatus.OK);
    }

    @PostMapping("/write")
    public ResponseEntity<ArticleDto> writeArticle(@RequestBody ArticleDto dto) {
        service.write(dto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteArticle(@RequestBody ArticleDto dto) {
        service.delete(dto);
        return new ResponseEntity<>("글 삭제 완료", HttpStatus.OK);
    }
}

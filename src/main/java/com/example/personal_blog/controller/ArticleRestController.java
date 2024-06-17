package com.example.personal_blog.controller;

import com.example.personal_blog.model.dto.ArticleDto;
import com.example.personal_blog.service.ArticleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/article/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id) {
        return new ResponseEntity<>(service.read(id), HttpStatus.OK);
    }

    @PutMapping("/article/{id}/update")
    public ResponseEntity<String> updateArticle(@RequestBody ArticleDto dto, @PathVariable Long id ){
        service.update(dto, id);
        return new ResponseEntity<>("글 수정 완료", HttpStatus.OK);
    }

    @PostMapping("/write")
    public ResponseEntity<ArticleDto> writeArticle(@RequestBody ArticleDto dto) {
        service.write(dto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/article/{id}/delete")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id){
        service.delete(id);
        return new ResponseEntity<>("글 삭제 완료", HttpStatus.OK);
    }
}

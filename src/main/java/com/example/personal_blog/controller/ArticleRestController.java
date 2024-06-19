package com.example.personal_blog.controller;

import com.example.personal_blog.model.ArticleDto;
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

    /**
     * 게시글 작성
     * @param dto
     */
    @PostMapping("/write")
    public ResponseEntity<ArticleDto> writeArticle(@RequestBody ArticleDto dto) {
        service.write(dto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * 게시글 목록 조회
     * @return
     */
    @GetMapping("/articles")
    public ResponseEntity<List<ArticleDto>> getArticleList() {
        return new ResponseEntity<>(service.getArticleList(), HttpStatus.OK);
    }

    /**
     * 특정 게시글 조회
     * @param id
     */
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id) {
        return new ResponseEntity<>(service.read(id), HttpStatus.OK);
    }
  
    /**
     * 특정 게시글 편집
     * @param dto
     */
    @PutMapping("/articles/{id}/update")
    public ResponseEntity<String> updateArticle(@RequestBody ArticleDto dto) {
        String response = service.update(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 특정 게시글 삭제
     * @param dto
     */
    @PutMapping("/articles/{id}/delete")
    public ResponseEntity<String> deleteArticle(@RequestBody ArticleDto dto) {
        String response = service.delete(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 게시글 좋아요
     * @param id
     */
    @PutMapping("/articles/{id}/like")
    public ResponseEntity<String> likeArticle(@PathVariable Long id) {
        service.addLike(id);
        return new ResponseEntity<>("Likes!", HttpStatus.OK);
    }

    /**
     * 게시글 좋아요 취소
     * @param id
     */
    @PutMapping("/articles/{id}/cancel-like")
    public ResponseEntity<String> cancelLikeArticle(@PathVariable Long id) {
        service.cancelLike(id);
        return new ResponseEntity<>("Likes canceled", HttpStatus.OK);
    }
}

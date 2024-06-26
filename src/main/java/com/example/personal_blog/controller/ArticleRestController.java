package com.example.personal_blog.controller;

import com.example.personal_blog.model.ArticleDto;
import com.example.personal_blog.service.ArticleService;
import com.example.personal_blog.service.ContentPathService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ArticleRestController {

    private final ArticleService articleService;
    private final ContentPathService contentPathService;

    @GetMapping("/hello")
    public String home() {
        return "Hello World!";
    }

    /**
     * 게시글 작성
     * @param dto
     */
    @PostMapping("/write")
    @Transactional
    public ResponseEntity<ArticleDto> writeArticle(
        @RequestPart("article") ArticleDto dto,
        @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {

        ArticleDto savedDto = articleService.write(dto);

        if (files != null) {
            for (MultipartFile file : files) {
                contentPathService.saveImages(file, savedDto);
            }
        } else {
            System.out.println("No files attached");
        }

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * 게시글 목록 조회
     * @return
     */
    @GetMapping("/articles")
    public ResponseEntity<List<ArticleDto>> getArticleList() {
        return new ResponseEntity<>(articleService.getArticleList(), HttpStatus.OK);
    }

    /**
     * 특정 게시글 조회
     * @param id
     */
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id) throws IOException {


        return new ResponseEntity<>(articleService.read(id), HttpStatus.OK);
    }
  
    /**
     * 특정 게시글 편집
     * @param dto
     */
    @PutMapping("/articles/{id}/update")
    public ResponseEntity<String> updateArticle(@RequestBody ArticleDto dto, @PathVariable Long id) {
        String response = articleService.update(dto, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 특정 게시글 삭제
     * @param id
     */
    @PutMapping("/articles/{id}/delete")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {
        String response = articleService.delete(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 게시글 좋아요
     * @param id
     */
    @PutMapping("/articles/{id}/like")
    public ResponseEntity<String> likeArticle(@PathVariable Long id) {
        articleService.addLike(id);
        return new ResponseEntity<>("Likes!", HttpStatus.OK);
    }

    /**
     * 게시글 좋아요 취소
     * @param id
     */
    @PutMapping("/articles/{id}/cancel-like")
    public ResponseEntity<String> cancelLikeArticle(@PathVariable Long id) {
        articleService.cancelLike(id);
        return new ResponseEntity<>("Likes canceled", HttpStatus.OK);
    }
}

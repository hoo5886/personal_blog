package com.example.personal_blog.controller;

import com.example.personal_blog.dto.ArticleDto;
import com.example.personal_blog.service.ArticleService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/hello")
    public String home() {
        return "Hello World!";
    }

    /**
     * 게시글 저장
     * @param dto
     */
    @PostMapping("/write")
    @Transactional
    public ResponseEntity<ArticleDto> writeArticle(
        @RequestPart("article") ArticleDto dto,
        @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {

        return new ResponseEntity<>(articleService.write(dto, files), HttpStatus.CREATED);
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
     * @param articleId
     */
    @GetMapping("/articles/{articleId}")
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long articleId) throws IOException {
        var articleDto = articleService.readArticle(articleId);

        return new ResponseEntity<>(articleDto, HttpStatus.OK);
    }
  
    /**
     * 특정 게시글 편집
     * 게시글 편집 후 '저장'버튼을 누르면 수정된 게시글을 ArticleDto으로 클라이언트로부터 가져온다.
     * ArticleDto에 담겨있는 정보에 더해 이미지 정보까지 업데이트한다.
     * 이미지 업데이트는 해당 글이 가지고 있는 이미지를 일괄 삭제 후, 다시 저장하는 방식으로 한다.
     * @param articleId
     * @param articleDto
     */
    @PostMapping("/articles/{articleId}/update")
    public ResponseEntity<String> updateArticle(@RequestPart("article") ArticleDto articleDto,
                                                @PathVariable Long articleId,
                                                @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException{
        String response = articleService.update(articleId, articleDto, files);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 특정 게시글 삭제
     * @param articleId
     */
    @PutMapping("/articles/{articleId}/delete")
    public ResponseEntity<String> deleteArticle(@PathVariable Long articleId) {
        String response = articleService.delete(articleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 게시글 좋아요
     * @param articleId
     */
    @PutMapping("/articles/{articleId}/like")
    public ResponseEntity<String> likeArticle(@PathVariable Long articleId) {
        articleService.addLike(articleId);
        return new ResponseEntity<>("Likes!", HttpStatus.OK);
    }

    /**
     * 게시글 좋아요 취소
     * @param articleId
     */
    @PutMapping("/articles/{articleId}/cancel-like")
    public ResponseEntity<String> cancelLikeArticle(@PathVariable Long articleId) {
        articleService.cancelLike(articleId);
        return new ResponseEntity<>("Likes canceled", HttpStatus.OK);
    }
}

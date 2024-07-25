package com.example.personal_blog.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.ContentPath;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest // DataJpaTest에는 @Transactional이 기본적으로 포함되어 있음
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ContentPathRepository contentPathRepository;

    Article article;

    @BeforeEach
    void setUpArticle() {
        article = Article.builder()
            .articleId(1L)
            .title("제목")
            .content("내용")
            .hits(0)
            .likes(0)
            .isDeleted(false)
            .contentPaths(Set.of())
            .build();
    }

    @Test
    @DisplayName("게시글 저장: 이미지 포함")
    void saveArticleWithImages() {
        // given
        var contentPath1 = ContentPath.builder()
            .contentPath("test/path/testImage1.png")
            .article(article)
            .build();

        var contentPath2 = ContentPath.builder()
            .contentPath("test/path/testImage2.png")
            .article(article)
            .build();

        // when
        articleRepository.save(article);
        contentPathRepository.save(contentPath1);
        contentPathRepository.save(contentPath2);

        // then
        var contentPaths = contentPathRepository.findByArticleId(
            article.getArticleId()).get();
        var articles = articleRepository.findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        assertThat(contentPaths.size()).isEqualTo(2);
        assertThat(articles.getTitle()).isEqualTo("제목");
        assertThat(articles.getContent()).isEqualTo("내용");
        assertThat(contentPaths).extracting("contentPath")
            .contains("test/path/testImage1.png", "test/path/testImage2.png");
    }

    @Test
    @DisplayName("게시글 저장: 이미지 미포함")
    void saveArticleWithoutImages() {
        // given : beforeEach 참조

        // when
        articleRepository.save(article);

        // then
        var articles = articleRepository.findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        assertThat(articles.getTitle()).isEqualTo("제목");
        assertThat(articles.getContent()).isEqualTo("내용");
        assertThat(articles.getContentPaths().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글 불러오기: 이미지 포함")
    void loadImages() {
        // given
        var contentPath1 = ContentPath.builder()
            .contentPath("test/path/testImage1.png")
            .article(article)
            .build();

        var contentPath2 = ContentPath.builder()
            .contentPath("test/path/testImage2.png")
            .article(article)
            .build();

        // when
        articleRepository.save(article);
        contentPathRepository.save(contentPath1);
        contentPathRepository.save(contentPath2);

        // then
        var contentPaths = contentPathRepository.findByArticleId(
            article.getArticleId()).get();

        assertThat(contentPaths.size()).isEqualTo(2);
        assertThat(contentPaths).extracting("contentPath")
            .contains("test/path/testImage1.png", "test/path/testImage2.png");
    }

    @Test
    @DisplayName("게시글 삭제하기")
    void deleteArticle() {
        // given : beforeEach 참조
        articleRepository.save(article);

        // when
        var article2 = articleRepository.findById(article.getArticleId())
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        article2.setDeleted(true);
        articleRepository.save(article2);

        // then
        Article editedArticle = articleRepository.findById(article2.getArticleId())
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        assertThat(editedArticle.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("게시글 수정하기")
    void editArticle() {
        // given : beforeEach 참조
        articleRepository.save(article);
        var updatedAt = LocalDateTime.now();

        // when
        var article2 = articleRepository.findById(article.getArticleId())
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        article2.setTitle("수정된 제목");
        article2.setContent("수정된 내용");
        articleRepository.save(article2);

        //then
        var editedArticle = articleRepository.findById(article2.getArticleId())
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        assertThat(editedArticle.getTitle()).isEqualTo("수정된 제목");
        assertThat(editedArticle.getContent()).isEqualTo("수정된 내용");
        assertThat(editedArticle.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
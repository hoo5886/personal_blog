package com.example.personal_blog;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.repository.ArticleRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest // DataJpaTest에는 @Transactional이 기본적으로 포함되어 있음
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTest {

    @Autowired
    private ArticleRepository repository;

    @Test
    void save() {
        // given
        Article article = Article.builder()
            .title("제목")
            .content("내용")
            .hits(0)
            .likes(0)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        // when
        repository.save(article);

        // then
        List<Article> articles = repository.findAll();
        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo("제목");
    }

}

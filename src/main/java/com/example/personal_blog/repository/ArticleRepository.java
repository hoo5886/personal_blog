package com.example.personal_blog.repository;

import com.example.personal_blog.entity.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.comments WHERE a.articleId = :articleId")
    Optional<Article> findByIdWithComments(@Param("articleId") Long articleId);
}

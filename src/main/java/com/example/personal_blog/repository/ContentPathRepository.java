package com.example.personal_blog.repository;

import com.example.personal_blog.entity.ContentPath;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentPathRepository extends JpaRepository<ContentPath, Long> {

    @Query("SELECT c FROM content_path c WHERE c.article.articleId = :articleId")
    Optional<Set<ContentPath>> findByArticleId(@Param("articleId") Long articleId);

    @Query("DELETE FROM content_path c WHERE c.article.articleId = :articleId")
    void deleteAllByArticleId(@Param("articleId") Long articleId);

//    Optional<Object> findAllByArticleId(Long articleId);
}

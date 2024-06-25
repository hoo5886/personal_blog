package com.example.personal_blog.repository;

import com.example.personal_blog.entity.ContentPath;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentPathRepository extends JpaRepository<ContentPath, Long> {

//    @Query("SELECT c.contentPath FROM ContentPath c WHERE c.articleId = :articleId")
//    Optional<Set<ContentPath>> findByArticleId(Long articleId);
}

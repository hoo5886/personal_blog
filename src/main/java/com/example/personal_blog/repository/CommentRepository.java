package com.example.personal_blog.repository;

import com.example.personal_blog.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.article.articleId = :articleId")
    Optional<List<Comment>> findAllByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT c FROM Comment c WHERE c.user.userId = :userId")
    Optional<List<Comment>> findAllByUserId(@Param("userId") Long userId);
}

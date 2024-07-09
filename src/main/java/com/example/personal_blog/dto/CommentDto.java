package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.Comment;
import com.example.personal_blog.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record CommentDto(
    Long commentId,
    String nickname,
    String comment,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long userId,
    Long articleId
) {
    public CommentDto {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
            .commentId(comment.getCommentId())
            .nickname(comment.getNickname())
            .comment(comment.getComment())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .userId(comment.getUser().getUserId())
            .articleId(comment.getArticle().getArticleId())
            .build();
    }

    /*
    * dto 에서 엔티티로 변환할 때는 createdAt, updatedAt필드는 자동 생성되어서 별도로 처리x
    * */
    public static Comment to(CommentDto dto, Article article, User user) {
        return Comment.builder()
                .commentId(dto.commentId())
                .nickname(dto.nickname())
                .comment(dto.comment())
                .article(article)
                .user(user)
                .build();
    }
}

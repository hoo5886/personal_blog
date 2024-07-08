package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentDto(
    Long commentId,
    String nickname,
    String comment,
    UserDto userDto,
    ArticleDto articleDto,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
            .commentId(comment.getCommentId())
            .nickname(comment.getNickname())
            .comment(comment.getComment())
            .userDto(UserDto.from(comment.getUser()))
            .articleDto(ArticleDto.from(comment.getArticle()))
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .build();
    }

    /*
    * dto 에서 엔티티로 변환할 때는 createdAt, updatedAt필드는 자동 생성되어서 별도로 처리x
    * */
    public static Comment to(CommentDto dto) {
        return Comment.builder()
                .commentId(dto.commentId())
                .nickname(dto.nickname())
                .comment(dto.comment())
                .article(ArticleDto.to(dto.articleDto()))
                .user(UserDto.to(dto.userDto()))
                .build();
    }
}

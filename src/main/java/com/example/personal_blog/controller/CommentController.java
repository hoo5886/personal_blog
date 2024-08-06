package com.example.personal_blog.controller;

import com.example.personal_blog.dto.CommentDto;
import com.example.personal_blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성
     * @param articleId
     * @param commentDto
     * @return
     */
    @PostMapping("/{articleId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long articleId,
        @RequestBody CommentDto commentDto) {

        CommentDto createdComment = commentService.createComment(articleId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    /**
     * 댓글 수정
     * @param articleId
     * @param commentId
     * @param commentDto
     * @return
     */
    @PutMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long articleId,
        @PathVariable Long commentId, @RequestBody CommentDto commentDto) {

        CommentDto updatedComment = commentService.updateComment(articleId, commentDto, commentId);
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * 댓글 삭제
     * @param articleId
     * @param commentId
     * @return
     */
    @DeleteMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long articleId, @PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}

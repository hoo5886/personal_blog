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
@RequestMapping("/articles/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable Long articleId, @RequestBody CommentDto commentDto) {

        CommentDto createdComment = commentService.createComment(articleId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long articleId, @PathVariable Long commentId, @RequestBody CommentDto commentDto) {

        CommentDto updatedComment = commentService.updateComment(articleId, commentDto, commentId);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long articleId, @PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}

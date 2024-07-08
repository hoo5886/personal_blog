package com.example.personal_blog.service;

import com.example.personal_blog.dto.CommentDto;
import com.example.personal_blog.repository.ArticleRepository;
import com.example.personal_blog.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public List<CommentDto> getCommentsByUser(Long userId) {
        var comments = commentRepository.findAllByUserId(userId);

        return comments.map(commentList -> commentList.stream()
            .map(CommentDto::from)
            .collect(Collectors.toList())).orElseGet(List::of);
    }

    public CommentDto getComment(Long commentId) {
        var comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        return CommentDto.from(comment);
    }

    public CommentDto createComment(Long articleId, CommentDto commentDto) {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        var comment = CommentDto.to(commentDto);
        comment.setArticle(article);
        commentRepository.save(comment);

        return CommentDto.from(comment);
    }

    public CommentDto updateComment(Long articleId, CommentDto commentDto, Long commentId) {
        var comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if (!comment.getArticle().getArticleId().equals(articleId)) {
            throw new IllegalArgumentException("댓글이 게시글에 속해있지 않습니다.");
        }

        comment.setComment(commentDto.comment());
        commentRepository.save(comment);

        return CommentDto.from(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}

package com.example.personal_blog.service;

import com.example.personal_blog.dto.CommentDto;
import com.example.personal_blog.event.CommentAddedEvent;
import com.example.personal_blog.repository.ArticleRepository;
import com.example.personal_blog.repository.CommentRepository;
import com.example.personal_blog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

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

        var user = userRepository.findById(commentDto.userId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        var savedComment = CommentDto.to(commentDto, article, user);
        eventPublisher.publishEvent(new CommentAddedEvent(savedComment));

        savedComment.setArticle(article);
        commentRepository.save(savedComment);

        log.info("댓글을 생성했습니다. : {}", savedComment.getComment());
        return CommentDto.from(savedComment);
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

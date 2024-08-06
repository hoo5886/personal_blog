package com.example.personal_blog.service;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.ContentPath;
import com.example.personal_blog.dto.ContentPathDto;
import com.example.personal_blog.event.ArticleLikedEvent;
import com.example.personal_blog.repository.ArticleRepository;
import com.example.personal_blog.dto.ArticleDto;
import com.example.personal_blog.repository.CommentRepository;
import com.example.personal_blog.repository.ContentPathRepository;
import com.example.personal_blog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ContentPathService contentPathService;
    private final ContentPathRepository contentPathRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 게시글 목록 조회
     * @return
     */
    public List<ArticleDto> getArticleList() {
        List<Article> entity = articleRepository.findAll();
        List<ArticleDto> dtoList = new ArrayList<>();

        for (Article article : entity) {
           dtoList.add(ArticleDto.from(article));
        }
        return dtoList;
    }

    /**
     * 게시글 조회
     *   댓글포함
     * @param articleId
     * @return ArticleDto
     * @throws IOException
     */
    public ArticleDto readArticle(Long articleId) throws IOException {
        var article = articleRepository.findByIdWithComments(articleId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        //조회수
        article.setHits(article.getHits() + 1);

        //ContentPath로부터 이미지 가져오기.
        Set<ContentPathDto> contentPathDtos = contentPathService.getImagePaths(articleId);
        for (ContentPathDto contentPath : contentPathDtos) {
            article.addContentPath(ContentPathDto.to(contentPath));
        }
        articleRepository.save(article);

        return ArticleDto.from(article);
    }

    /**
     * 게시글 조회(읽기 전용)
     * @param articleId
     * @return
     */
    public ArticleDto readOnlyArticle(Long articleId) {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        return ArticleDto.from(article);
    }

    /**
     * 게시글 수정
     * @param articleId
     * @return
     */
    public String update(Long articleId, ArticleDto dto, MultipartFile[] files) throws IOException {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. articleId: " + articleId));

        if (files != null) {
            contentPathService.updateImages(files, article);
        }

        article.setTitle(dto.title());
        article.setContent(dto.content());
        article.setUpdatedAt(LocalDateTime.now());
        articleRepository.save(article);

        return "수정된 글: " + article.getArticleId();
    }

    /**
     * 게시글 삭제
     * @param articleId
     * @return
     */
    public String delete(Long articleId) {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. articleId: " + articleId));

        article.setDeleted(true);
        articleRepository.save(article);

        return "게시글이 블라인드 처리됐습니다. :" + articleId;
    }

    @Transactional
    public void addLike(Long articleId) {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. articleId: " + articleId));
        article.setLikes(article.getLikes() + 1);

        eventPublisher.publishEvent(new ArticleLikedEvent(article));

        articleRepository.save(article);

        log.info("게시글 좋아요 이벤트를 발생시켰습니다.");
    }

    @Transactional
    public void cancelLike(Long articleId) {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. articleId: " + articleId));

        article.setLikes(article.getLikes() - 1);

        articleRepository.save(article);
    }


    /**
     * 게시글 작성
     * @param articleDto
     * @return
     */
    @Transactional
    public ArticleDto write(ArticleDto articleDto, MultipartFile[] files) throws IOException {
        var article = getUserByArticle(articleDto);
        articleRepository.save(article);

        Set<ContentPathDto> contentPathDtos = articleDto.contentPaths() != null ? articleDto.contentPaths() : new HashSet<>();

        if (!contentPathDtos.isEmpty()) {
            for (ContentPathDto contentPathDto : contentPathDtos) {
                ContentPath contentPath = new ContentPath();
                contentPath.setContentPath(contentPathDto.path());
                article.addContentPath(contentPath); // Article에 ContentPath 추가
            }
        }

        if (files != null) {
            contentPathService.saveImages(files, article);
        }

        return ArticleDto.from(article);
    }

    public Article getUserByArticle(ArticleDto articleDto) {

        //글을 작성한 유저, 글에 포함된 댓글들, 글이 가지고 있는 이미지 경로
        var user = userRepository.findById(articleDto.userId())
            .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        var comments = commentRepository.findAllByArticleId(articleDto.articleId()).get();

        var contentPaths = contentPathRepository.findByArticleId(articleDto.articleId())
            .orElse(new HashSet<>());

        return ArticleDto.to(articleDto, user, comments, contentPaths);
    }
}

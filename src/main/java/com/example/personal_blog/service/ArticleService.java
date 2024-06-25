package com.example.personal_blog.service;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.model.ArticleDto;
import com.example.personal_blog.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<ArticleDto> getArticleList() {
        List<Article> entity = articleRepository.findAll();
        List<ArticleDto> dtoList = new ArrayList<>();

        for (Article article : entity) {
           dtoList.add(ArticleDto.from(article));
        }
        return dtoList;
    }

    public ArticleDto read(Long articleId) {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        article.setHits(article.getHits() + 1);
        articleRepository.save(article);

        return ArticleDto.from(article);
    }

    public String update(ArticleDto dto, Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));

        article.setTitle(dto.title());
        article.setContent(dto.content());
        article.setUpdatedAt(LocalDateTime.now());
        articleRepository.save(article);

        return "수정된 글: " + dto.articleId();
    }

    public String delete(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));

        article.setDeleted(true);
        articleRepository.save(article);

        return "게시글이 블라인드 처리됐습니다. :" + id;
    }

    public void addLike(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));
        article.setLikes(article.getLikes() + 1);
        articleRepository.save(article);
    }

    public void cancelLike(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));

        article.setLikes(article.getLikes() - 1);

        articleRepository.save(article);
    }

    public ArticleDto write(ArticleDto dto) {
        var article = ArticleDto.to(dto);
        articleRepository.save(article);

        return ArticleDto.from(article);
    }
}

package com.example.personal_blog.service;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.repository.ArticleRepository;
import com.example.personal_blog.model.ArticleDto;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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

    public ArticleDto read(Long id) {
        var article = articleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        return ArticleDto.from(article);
    }

    public String update(ArticleDto dto) {
        Article article = articleRepository.findById(dto.id())
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + dto.id()));

        article.setTitle(dto.title());
        article.setContent(dto.content());
        articleRepository.save(article);

        return "수정된 글: " + dto.id();
    }

    public String delete(ArticleDto dto) {
        Article article = articleRepository.findById(dto.id())
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + dto.id()));

        article.setDeleted(true);
        articleRepository.save(article);

        return "게시글이 블라인드 처리됐습니다. :" + dto.id();
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

    public void write(ArticleDto dto) {
        var article = ArticleDto.to(dto);
        articleRepository.save(article);
    }
}

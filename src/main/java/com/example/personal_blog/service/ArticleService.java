package com.example.personal_blog.service;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.repository.ArticleRepository;
import com.example.personal_blog.model.dao.ArticleDao;
import com.example.personal_blog.model.dto.ArticleDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<ArticleDto> getArticleList() {
        List<Article> entity = articleRepository.findAll();
        List<ArticleDao> daoList = new ArrayList<>();
        List<ArticleDto> dtoList = new ArrayList<>();

        for (Article article : entity) {
           daoList.add(ArticleDao.from(article));
        }

        for (ArticleDao dao : daoList) {
            dtoList.add(ArticleDto.from(dao));
        }

        return dtoList;
    }

    public ArticleDto read(Long id) {
        var dao = ArticleDao.from(
            Objects.requireNonNull(articleRepository.findById(id).orElse(null)));

        return ArticleDto.from(dao);
    }

    public void delete(Long id) {
        var entity = articleRepository.findById(id).orElse(null);
        entity.setDeleted(true);
        articleRepository.save(entity);
    }

    public ArticleDto update(ArticleDto dto, Long id) {
        var entity = convertToEntity(dto);
        articleRepository.findById(id).ifPresent(article -> {
            article.setTitle(entity.getTitle());
            article.setContent(entity.getContent());
            article.setUpdatedAt(LocalDateTime.now());
            articleRepository.save(article);
        });

        return ArticleDto.from(ArticleDao.from(entity));
    }

    public void addLike(ArticleDto dto) {
        var entity = convertToEntity(dto);
        articleRepository.findById(entity.getId()).ifPresent(article -> {
            article.setLikes(entity.getLikes() + 1);
            articleRepository.save(article);
        });
    }

    public void write(ArticleDto dto) {
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        var entity = convertToEntity(dto);
        articleRepository.save(entity);
    }

    // DAO to Entity from DTO
    private Article convertToEntity(ArticleDto dto) {
        var dao = ArticleDao.from(dto);
        return ArticleDao.toEntity(dao);
    }
}

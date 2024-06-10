package com.example.personal_blog.service;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.repository.ArticleRepository;
import com.example.personal_blog.repository.dao.ArticleDao;
import com.example.personal_blog.repository.dto.ArticleDto;
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

    public String delete(ArticleDto dto) {
        var entity = convertToEntity(dto);
        articleRepository.deleteById(entity.getId());
        return "삭제된 글: " + dto.getId();
    }

    public String update(ArticleDto dto) {
        var entity = convertToEntity(dto);
        articleRepository.findById(dto.getId()).ifPresent(article -> {
            article.setTitle(entity.getTitle());
            article.setContent(entity.getContent());
            articleRepository.save(article);
        });

        return "수정된 글: " + dto.getId();
    }

    public void addLike(ArticleDto dto) {
        var entity = convertToEntity(dto);
        articleRepository.findById(entity.getId()).ifPresent(article -> {
            article.setLikes(entity.getLikes() + 1);
            articleRepository.save(article);
        });
    }

    public void write(ArticleDto dto) {
        var entity = convertToEntity(dto);
        articleRepository.save(entity);
    }

    // DAO to Entity from DTO
    private Article convertToEntity(ArticleDto dto) {
        var dao = ArticleDao.from(dto);
        return ArticleDao.toEntity(dao);
    }
}

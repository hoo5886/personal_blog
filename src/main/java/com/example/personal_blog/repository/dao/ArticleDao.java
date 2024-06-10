package com.example.personal_blog.repository.dao;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.repository.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDao {
    private Long id;
    private String title;
    private String content;
    private long hits;
    private int likes;
    private boolean isDeleted;
    private String createdAt;
    private String updatedAt;

    //  DTO to DAO
    public static ArticleDao from(ArticleDto dto) {
        return ArticleDao.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .likes(dto.getLikes())
                .createdAt(dto.getCreatedAt()) // String.valueOf(now())로써 DTO로부터 받는다.
                .build();
    }

    //  Entity to DAO
    public static ArticleDao from(Article entity) {
        return ArticleDao.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .likes(entity.getLikes())
                .createdAt(entity.getCreatedAt().toString())
                .build();
    }

    // DAO to DTO
    public static ArticleDto to(ArticleDao dao) {
        return ArticleDto.builder()
                .id(dao.getId())
                .title(dao.getTitle())
                .content(dao.getContent())
                .likes(dao.getLikes())
                .createdAt(dao.getCreatedAt())
                .build();
    }

    // DAO to Entity
    public static Article toEntity(ArticleDao dao) {
        return Article.builder()
                .id(dao.getId())
                .title(dao.getTitle())
                .content(dao.getContent())
                .likes(dao.getLikes())
                .build();
    }
}

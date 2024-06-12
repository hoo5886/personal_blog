package com.example.personal_blog.model.dao;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.model.dto.ArticleDto;
import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //  DTO to DAO
    public static ArticleDao from(ArticleDto dto) {
        return ArticleDao.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .likes(dto.getLikes())
                .hits(dto.getHits())
                .isDeleted(dto.isDeleted())
                .createdAt(dto.getCreatedAt()) // String.valueOf(now())로써 DTO로부터 받는다.
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    //  Entity to DAO
    public static ArticleDao from(Article entity) {
        return ArticleDao.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .likes(entity.getLikes())
                .hits(entity.getHits())
                .isDeleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // DAO to DTO
    public static ArticleDto to(ArticleDao dao) {
        return ArticleDto.builder()
                .id(dao.getId())
                .title(dao.getTitle())
                .content(dao.getContent())
                .likes(dao.getLikes())
                .hits(dao.getHits())
                .isDeleted(dao.isDeleted())
                .createdAt(dao.getCreatedAt())
                .updatedAt(dao.getUpdatedAt())
                .build();
    }

    // DAO to Entity
    public static Article toEntity(ArticleDao dao) {
        return Article.builder()
                .id(dao.getId())
                .title(dao.getTitle())
                .content(dao.getContent())
                .likes(dao.getLikes())
                .hits(dao.getHits())
                .isDeleted(dao.isDeleted())
                .createdAt(dao.getCreatedAt())
                .updatedAt(dao.getUpdatedAt())
                .build();
    }
}

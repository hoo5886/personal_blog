package com.example.personal_blog.model.dto;

import com.example.personal_blog.model.dao.ArticleDao;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private String title;
    private String content;
    private long hits;
    private int likes;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // DTO로부터 DAO로 변환
    public static ArticleDto from(ArticleDao dao) {
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

    // DAO로부터 DTO로 변환
    public static ArticleDao to(ArticleDto dto) {
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
}

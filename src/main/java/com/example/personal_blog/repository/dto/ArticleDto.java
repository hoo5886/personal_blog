package com.example.personal_blog.repository.dto;

import static java.time.LocalTime.now;

import com.example.personal_blog.repository.dao.ArticleDao;
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
    private String createdAt;
    private String updatedAt;

    // DTO로부터 DAO로 변환
    public static ArticleDto from(ArticleDao dao) {
        return ArticleDto.builder()
            .id(dao.getId())
            .title(dao.getTitle())
            .content(dao.getContent())
            .likes(dao.getLikes())
            .createdAt(dao.getCreatedAt())
            .build();
    }

    // DAO로부터 DTO로 변환
    public static ArticleDao to(ArticleDto dto) {
        return ArticleDao.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .content(dto.getContent())
            .likes(dto.getLikes())
            .createdAt(dto.getCreatedAt()) // String.valueOf(now())로써 DTO로부터 받는다.
            .build();
    }
}

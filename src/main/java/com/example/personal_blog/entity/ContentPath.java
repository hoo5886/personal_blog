package com.example.personal_blog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Entity(name = "content_path")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentPathId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private String contentPath;

    @CreatedDate
    private LocalDateTime createdAt;
}

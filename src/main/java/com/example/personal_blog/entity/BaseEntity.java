package com.example.personal_blog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
@Getter
public abstract sealed class BaseEntity permits Article, Comment, User, Authority {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt = null;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

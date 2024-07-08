package com.example.personal_blog.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "title")
    private String title;

    @Column(columnDefinition = "MEDIUMTEXT", name = "content")
    private String content;

    @Column(name = "hits")
    private long hits; //조회수

    @Column(name = "likes")
    private int likes; //공감

    @Column(name = "is_deleted")
    private boolean isDeleted; //삭제여부

    @OneToMany(mappedBy = "article",
        cascade = CascadeType.ALL,
//        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private Set<ContentPath> contentPaths;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    public void addContentPath(ContentPath contentPath) {
        contentPaths.add(contentPath);
        contentPath.setArticle(this);
    }

    public void removeContentPath(ContentPath contentPath) {
        contentPaths.remove(contentPath);
        contentPath.setArticle(null);
    }
}

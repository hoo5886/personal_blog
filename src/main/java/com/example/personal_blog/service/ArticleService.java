package com.example.personal_blog.service;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.ContentPath;
import com.example.personal_blog.model.ArticleDto;
import com.example.personal_blog.model.ContentPathDto;
import com.example.personal_blog.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    private final ContentPathService contentPathService;

    public List<ArticleDto> getArticleList() {
        List<Article> entity = articleRepository.findAll();
        List<ArticleDto> dtoList = new ArrayList<>();

        for (Article article : entity) {
           dtoList.add(ArticleDto.from(article));
        }
        return dtoList;
    }

    public ArticleDto read(Long articleId) throws IOException {
        var article = articleRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        //조회수
        article.setHits(article.getHits() + 1);

        //ContentPath로부터 이미지 가져오기.
        Set<ContentPathDto> contentPaths = contentPathService.getImagePaths(articleId);
        for (ContentPathDto contentPath : contentPaths) {
            contentPath.path();
        }

        Set<ContentPath> contentPathSet = contentPaths.stream()
            .map(ContentPathDto::to)
            .collect(Collectors.toSet());

        article.setContentPaths(contentPathSet);
        articleRepository.save(article);

        return ArticleDto.from(article);
    }

    public String update(ArticleDto dto, Long id) {
        var article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));

        article.setTitle(dto.title());
        article.setContent(dto.content());
        articleRepository.save(article);

        return "수정된 글: " + dto.articleId();
    }

    public String delete(Long id) {
        var article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));

        article.setDeleted(true);
        articleRepository.save(article);

        return "게시글이 블라인드 처리됐습니다. :" + id;
    }

    public void addLike(Long id) {
        var article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));
        article.setLikes(article.getLikes() + 1);
        articleRepository.save(article);
    }

    public void cancelLike(Long id) {
        var article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다. id: " + id));

        article.setLikes(article.getLikes() - 1);

        articleRepository.save(article);
    }

    public ArticleDto write(ArticleDto articleDto) {
        var article = ArticleDto.to(articleDto);
        articleRepository.save(article);

        Set<ContentPathDto> contentPathDtos = articleDto.contentPaths() != null ? articleDto.contentPaths() : new HashSet<>();

        for (ContentPathDto contentPathDto : contentPathDtos) {
            ContentPath contentPath = new ContentPath();
            contentPath.setContentPath(contentPathDto.path());
            article.addContentPath(contentPath); // Article에 ContentPath 추가
        }

        return ArticleDto.from(article);
    }
}

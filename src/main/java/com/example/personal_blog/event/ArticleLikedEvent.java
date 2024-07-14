package com.example.personal_blog.event;

import com.example.personal_blog.entity.Article;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArticleLikedEvent {

    private final Article article;

}

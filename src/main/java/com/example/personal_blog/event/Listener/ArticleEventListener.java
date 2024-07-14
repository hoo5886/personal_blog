package com.example.personal_blog.event.Listener;

import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.event.ArticleLikedEvent;
import com.example.personal_blog.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleArticleLikedEvent(ArticleLikedEvent event) {
        var article = event.getArticle();
        var notification = Notification.builder()
            .message("게시글이 좋아요를 받았습니다.")
            .user(article.getUser())
            .createdTime(article.getCreatedAt())
            .checkTime(null)
            .build();

        notificationService.createNotification(notification);
    }


}

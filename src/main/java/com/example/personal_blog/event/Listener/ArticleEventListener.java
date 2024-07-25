package com.example.personal_blog.event.Listener;

import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.event.ArticleLikedEvent;
import com.example.personal_blog.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
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
        var message = String.format("%s님의 %s 게시글이 좋아요를 받았습니다.", article.getUser().getUsername(), article.getTitle());

        var notification = Notification.builder()
            .message(message)
            .user(article.getUser())
            .createdTime(article.getCreatedAt())
            .checkTime(null)
            .build();
        log.info("알림객체를 생성했습니다. : {}", notification.getMessage());

        notificationService.createNotification(notification);
        notificationService.sendNotification(notification, article.getUser());
        log.info("게시글 좋아요 알림을 보냈습니다.");
    }
}

package com.example.personal_blog.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.example.personal_blog.entity.Article;
import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.entity.User;
import com.example.personal_blog.event.ArticleLikedEvent;
import com.example.personal_blog.event.Listener.ArticleEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotificationServiceTest {

    Logger log = LoggerFactory.getLogger(NotificationServiceTest.class);

    @Autowired
    private ArticleEventListener articleEventListener;

    @MockBean
    private NotificationService notificationService;

    private Article article;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
            .userId(1L)
            .loginId("testLoginId")
            .username("testUsername")
            .build();

        article = Article.builder()
            .articleId(1L)
            .title("testTitle")
            .content("testContent")
            .user(user)
            .build();
    }

    @Test
    public void testHandleArticleLikedEvent() {
        // Given
        var event = new ArticleLikedEvent(article);

        // Mocking the service
        doNothing().when(notificationService).sendNotification(any(), any());

        // When
        articleEventListener.handleArticleLikedEvent(event);

        // Then
        verify(notificationService).sendNotification(any(Notification.class), any(User.class));
    }
}
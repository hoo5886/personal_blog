package com.example.personal_blog.event.Listener;

import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.event.CommentAddedEvent;
import com.example.personal_blog.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommentEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleCommentAddedEvent(CommentAddedEvent event) {
        var message = String.format("%s님이 %s에 댓글을 추가했습니다.",
            event.getComment().getUser().getUsername(),
            event.getComment().getArticle().getTitle()
        );

        var comment = event.getComment();
        var notification = Notification.builder()
            .message(message)
            .user(comment.getUser())
            .createdTime(comment.getCreatedAt())
            .checkTime(null)
            .build();
        log.debug("알림객체를 생성했습니다. : {}", notification.getMessage());

        notificationService.createNotification(notification);
        notificationService.sendNotification(notification, comment.getUser());
        log.debug("댓글 알림을 보냈습니다.");
    }
}

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
        var comment = event.getComment();
        var notification = Notification.builder()
            .message("댓글이 추가되었습니다.")
            .user(comment.getUser())
            .createdTime(comment.getCreatedAt())
            .checkTime(null)
            .build();

        notificationService.createNotification(notification);
    }
}

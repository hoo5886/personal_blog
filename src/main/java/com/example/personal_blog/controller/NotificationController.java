package com.example.personal_blog.controller;

import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자가 알람을 확인한 경우, Controller를 통해 메시지를 request하고
     * 해당 메시지를 response로 반환한다.
     * @param notification
     * @return
     */
    @MessageMapping("/check-notification")
    @SendTo("/topic/notification/check")
    public Notification checkNotification(Notification notification) {
        log.debug("Received check-notification request: {}", notification);
        notificationService.checkNotification(notification.getNotificationId());
        log.debug("Sending response: {}", notification);
        return notification;
    }
}

package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record NotificationDto(
    Long notificationId,
    Long userId,
    String message,
    LocalDateTime createdTime,
    LocalDateTime checkTime
) {

    public static NotificationDto from(Notification notification) {
        return NotificationDto.builder()
            .notificationId(notification.getNotificationId())
            .userId(notification.getUser().getUserId())
            .message(notification.getMessage())
            .createdTime(notification.getCreatedTime())
            .checkTime(notification.getCheckTime())
            .build();
    }

    public static Notification to(NotificationDto notificationDto, User user) {
        return Notification.builder()
            .notificationId(notificationDto.notificationId())
            .user(user)
            .message(notificationDto.message())
            .createdTime(notificationDto.createdTime())
            .checkTime(notificationDto.checkTime())
            .build();
    }
}

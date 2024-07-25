package com.example.personal_blog.dto;

import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record NotificationDto(
    Long notificationId,
    Long userId,
    String username,
    String message,

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime createdTime,

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime checkTime
) {
    public static NotificationDto from(Notification notification) {
        return NotificationDto.builder()
            .notificationId(notification.getNotificationId())
            .userId(notification.getUser().getUserId())
            .username(notification.getUser().getUsername())
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

package com.example.personal_blog.service;

import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.entity.User;
import com.example.personal_blog.repository.NotificationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; //웹소켓

    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void sendNotification(Notification notification, User receiver) {
        messagingTemplate.convertAndSendToUser(receiver.getLoginId(), "/topic/notification", notification.getMessage());
    }

    /**
     * 당사자가 알림을 확인한 경우
     * @param notificationId
     */
    public void checkNotification(Long notificationId) {
        var notification = notificationRepository.findById(notificationId).orElseThrow(
            () -> new IllegalArgumentException("알림이 존재하지 않습니다.")
        );
        notification.setCheckTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}

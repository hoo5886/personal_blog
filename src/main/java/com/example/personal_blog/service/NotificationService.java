package com.example.personal_blog.service;

import com.example.personal_blog.dto.NotificationDto;
import com.example.personal_blog.entity.Notification;
import com.example.personal_blog.entity.User;
import com.example.personal_blog.repository.NotificationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; //Client와 Server간의 메시지 교환을 위한 객체

    @Transactional
    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    /**
     * 알림을 받는 사용자에게 알림을 전송
     * @param notification
     * @param receiver
     */
    public void sendNotification(Notification notification, User receiver) {
        log.info("알림을 전송합니다. : {}", notification.getMessage());
        NotificationDto notificationDto = NotificationDto.from(notification);

        messagingTemplate.
            convertAndSendToUser(
                receiver.getLoginId(), //사용자 로그인ID testId
                "/topic/notification", //사용자 목적지
                notificationDto // 알림 메시지
            );
        log.info("Notification sent to /user/{}{}", receiver.getLoginId(), "/topic/notification");
    }

    /**
     * 사용자가 알림을 확인한 경우
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

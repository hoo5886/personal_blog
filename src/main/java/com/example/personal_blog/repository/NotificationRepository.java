package com.example.personal_blog.repository;

import com.example.personal_blog.entity.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId")
    Optional<List<Notification>> findAllByUserId(@Param("userId") Long receiverId);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId ORDER BY n.createdTime DESC")
    Optional<Notification> findRecentNotificationByUserId(@Param("userId") Long receiverId);
}

package com.example.projetTechnique.repository;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientId(Long recipientId);
    List<Notification> findByRecipientIdAndPostIdAndType(Long recipientId, Long postId, NotificationType type);
}

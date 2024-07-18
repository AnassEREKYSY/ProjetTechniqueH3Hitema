package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Notification;
import com.example.projetTechnique.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Long recipientId, Long postId, String message, NotificationType type) {
        if (type == NotificationType.LIKE) {
            List<Notification> existingLikes = notificationRepository.findByRecipientIdAndPostIdAndType(recipientId, postId, type);
            if (!existingLikes.isEmpty()) {
                throw new IllegalStateException("A like notification already exists for this user and post.");
            }
        }

        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setPostId(postId);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setType(type);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientId(userId);
    }
}
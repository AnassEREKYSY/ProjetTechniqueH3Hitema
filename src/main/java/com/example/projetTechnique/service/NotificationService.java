package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Notification;
import com.example.projetTechnique.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public ResponseEntity<?> createNotification(Long recipientId, Long postId, String message, NotificationType type) {
        if (type == NotificationType.LIKE) {
            List<Notification> existingLikes = notificationRepository.findByRecipientIdAndPostIdAndType(recipientId, postId, type);
            if (!existingLikes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A like notification already exists for this user and post");
            }
        }
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setPostId(postId);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setType(type);
        notificationRepository.save(notification);
        return ResponseEntity.ok(notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientId(userId);
    }
}
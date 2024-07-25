package com.example.projetTechnique.service;

import com.example.projetTechnique.model.NotificationMessage;
import com.example.projetTechnique.repository.NotificationMessageRepository;
import com.example.projetTechnique.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class NotificationMessageService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    public ResponseEntity<?> createMessageNotification(Long recipientId, Long senderID, String message) {
        NotificationMessage notification = new NotificationMessage();
        notification.setRecipientId(recipientId);
        notification.setSenderID(senderID);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notificationMessageRepository.save(notification);
        return ResponseEntity.ok(notification);
    }

    public List<NotificationMessage> getNotificationsForSender(Long senderID) {
        return notificationMessageRepository.findBySenderID(senderID);
    }

    public List<NotificationMessage> getNotificationsForReceiver(Long receiverID) {
        return notificationMessageRepository.findByRecipientId(receiverID);
    }
}
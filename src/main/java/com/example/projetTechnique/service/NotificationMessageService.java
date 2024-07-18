package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Notification;
import com.example.projetTechnique.model.NotificationMessage;
import com.example.projetTechnique.repository.NotificationMessageRepository;
import com.example.projetTechnique.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationMessageService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    public NotificationMessage createMessageNotification(Long recipientId, Long senderID,String message) {
        NotificationMessage notification = new NotificationMessage();
        notification.setRecipientId(recipientId);
        notification.setSenderID(senderID);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        return notificationMessageRepository.save(notification);
    }

    public List<NotificationMessage> getNotificationsForSender(Long senderID) {
        return notificationMessageRepository.findBySenderID(senderID);
    }

    public List<NotificationMessage> getNotificationsForReceiver(Long receiverID) {
        return notificationMessageRepository.findByRecipientId(receiverID);
    }
}
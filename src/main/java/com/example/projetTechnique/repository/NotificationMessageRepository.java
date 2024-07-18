package com.example.projetTechnique.repository;

import com.example.projetTechnique.model.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
    List<NotificationMessage> findByRecipientId(Long recipientId);
    List<NotificationMessage> findBySenderID(long id);
}

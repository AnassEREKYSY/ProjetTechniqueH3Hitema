package com.example.projetTechnique.model;

import com.example.projetTechnique.Enum.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recipientId;
    private Long senderID;
    private String message;
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public Long getRecipientId() {
        return recipientId;
    }
    public String getMessage() {
        return message;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSenderID() {
        return senderID;
    }
    public void setSenderID(Long senderID) {
        this.senderID = senderID;
    }
}

package com.example.projetTechnique.model;

import com.example.projetTechnique.Enum.NotificationType;
import jakarta.persistence.*;
import org.aspectj.weaver.ast.Not;

import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recipientId;
    private Long postId;
    private String message;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Long getId() {
        return id;
    }

    public Long getRecipientId() {
        return recipientId;
    }
    public Long getPostId() {
        return postId;
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
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public NotificationType getType() {
        return type;
    }
    public void setType(NotificationType type) {
        this.type = type;
    }
}

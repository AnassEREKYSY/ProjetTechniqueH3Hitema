package com.example.projetTechnique.model;

import jakarta.persistence.*;
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
}

package com.example.projetTechnique.model;

import com.example.projetTechnique.Enum.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
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
}

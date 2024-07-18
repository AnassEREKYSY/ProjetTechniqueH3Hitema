package com.example.projetTechnique.controller;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Notification;
import com.example.projetTechnique.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/getAll/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestParam Long recipientId,
                                                           @RequestParam Long postId,
                                                           @RequestParam String message,
                                                           @RequestParam NotificationType type) {
        Notification notification = notificationService.createNotification(recipientId, postId, message, type);
        return ResponseEntity.ok(notification);
    }
}
package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.NotificationMessage;
import com.example.projetTechnique.service.NotificationMessageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/notificationMessage")
public class NotificationMessageController {

    @Autowired
    private NotificationMessageService notificationService;

    @GetMapping("/getAllSender/{senderID}")
    public ResponseEntity<List<NotificationMessage>> getUserNotificationsForSender(@PathVariable Long senderID) {
        return ResponseEntity.ok(notificationService.getNotificationsForSender(senderID));
    }

    @GetMapping("/getAllReceiver/{receiverID}")
    public ResponseEntity<List<NotificationMessage>> getUserNotificationsForReceiver(@PathVariable Long receiverID) {
        return ResponseEntity.ok(notificationService.getNotificationsForReceiver(receiverID));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNotification(@RequestParam Long recipientId,
                                                           @RequestParam Long senderID,
                                                           @RequestParam String message) {
        return ResponseEntity.ok(notificationService.createMessageNotification(recipientId, senderID, message));
    }
}
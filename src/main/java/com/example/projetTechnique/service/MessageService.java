package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Message;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.MessageRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationMessageService notificationMessageService;


    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<Message> getMessageById(Long id) {
        Message message=messageRepository.findById(id).orElse(null);
        if (message != null) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> sendMessage(Message message, String token , Long receiverId) {
        String jwtToken = token.substring(7);
        Long userId = userService.getLoggedInUserId(jwtToken);
        User sender=userService.findUserById(userId);
        User receiver=userService.findUserById(receiverId);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(new Date());
        messageRepository.save(message);
        String notif = "User " + sender.getUserName() + " sent you a message.";
        notificationMessageService.createMessageNotification(receiverId,sender.getId(),notif);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    public ResponseEntity<?> updateMessage(Long id, Message updatedMessage) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            Message TestMessage = updatedMessage;
            message.setContent(updatedMessage.getContent());
            messageRepository.save(message);
            if(TestMessage!=message) {
                String notif = "User " + message.getSender().getUserName() + " sent you a message.";
                notificationMessageService.createMessageNotification(message.getReceiver().getId(),message.getSender().getId(),notif);
                return ResponseEntity.ok(message);
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to update message\"}");
    }

    public ResponseEntity<?> deleteMessage(Long id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            messageRepository.deleteById(id);
            Optional<Message> TestMessage = messageRepository.findById(id);
            if(!TestMessage.isPresent()) {
                return ResponseEntity.ok("{\"message\":\"Message deleted successfully\"}");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete message\"}");
    }

    public ResponseEntity<List<Message>> getMessagesByUserToken(String token) {

        String valideToken=jwtUtil.extractToken(token);
        Long userId = jwtUtil.extractUserId(valideToken);
        User user = userRepository.findUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(user.getId(), user.getId());
        return ResponseEntity.ok(messages);
    }

}

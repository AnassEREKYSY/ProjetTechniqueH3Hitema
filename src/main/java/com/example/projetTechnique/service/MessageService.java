package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Message;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.MessageRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.security.JwtTokenProvider;
import com.example.projetTechnique.utilities.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
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

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<Message> getMessageById(Long id) {
        Optional<Message> message=messageRepository.findById(id);
        if (message.isPresent()) {
            return ResponseEntity.ok(message.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> sendMessage(Message message, String token , Long receiverId) {
        String jwtToken = jwtTokenProvider.extractToken(token);
        Long userId = userService.getLoggedInUserId(jwtToken);
        User sender=userService.findUserById(userId);
        User receiver=userService.findUserById(receiverId);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(new Date());
        messageRepository.save(message);
        String notif = "User " + sender.getUsername() + " sent you a message.";
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
                String notif = "User " + message.getSender().getUsername() + " sent you a message.";
                notificationMessageService.createMessageNotification(message.getReceiver().getId(),message.getSender().getId(),notif);
                return ResponseEntity.ok(message);
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update message");
    }

    public ResponseEntity<?> deleteMessage(Long id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            messageRepository.deleteById(id);
            Optional<Message> TestMessage = messageRepository.findById(id);
            if(!TestMessage.isPresent()) {
                return ResponseEntity.ok("Message deleted successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete message");
    }

    public ResponseEntity<List<Message>> getMessagesByUserToken(String token) {

        String valideToken=jwtTokenProvider.extractToken(token);
        Long userId = jwtUtil.extractUserId(valideToken);
        User user = userRepository.findUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(user.getId(), user.getId());
        return ResponseEntity.ok(messages);
    }

}

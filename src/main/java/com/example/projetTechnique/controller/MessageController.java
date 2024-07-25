package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Message;
import com.example.projetTechnique.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id);
    }

    @PostMapping("/sendMessage/{idReceiver}")
    public ResponseEntity<?> sendMessage(@PathVariable Long idReceiver, @RequestHeader("Authorization") String token,@RequestBody Message message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(message,token,idReceiver));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        return messageService.deleteMessage(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Long id, @RequestBody Message message) {
        return ResponseEntity.ok(messageService.updateMessage(id, message));
    }

    @GetMapping("/userMessages")
    public ResponseEntity<List<Message>> getMessagesByUserToken(@RequestHeader("Authorization") String token) {
        return messageService.getMessagesByUserToken(token);
    }
}

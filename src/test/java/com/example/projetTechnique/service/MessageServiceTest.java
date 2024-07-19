package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Message;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.MessageRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMessageService notificationMessageService;

    private User sender;
    private User receiver;
    private Message message;

    @BeforeEach
    public void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setUserName("Sender");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUserName("Receiver");

        message = new Message();
        message.setId(1L);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Hello");
        message.setTimestamp(new Date());
    }

    @Test
    public void testGetAllMessages() {
        when(messageRepository.findAll()).thenReturn(Collections.singletonList(message));

        ResponseEntity<List<Message>> response = messageService.getAllMessages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(message), response.getBody());
    }

    @Test
    public void testGetMessageByIdSuccess() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));

        ResponseEntity<Message> response = messageService.getMessageById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    public void testGetMessageByIdNotFound() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Message> response = messageService.getMessageById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testUpdateMessageSuccess() {
        Message updatedMessage = new Message();
        updatedMessage.setContent("Updated Content");

        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        ResponseEntity<?> response = messageService.updateMessage(1L, updatedMessage);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
        verify(notificationMessageService).createMessageNotification(2L, 1L, "User Sender sent you a message.");
    }

    @Test
    public void testUpdateMessageNotFound() {
        Message updatedMessage = new Message();
        updatedMessage.setContent("Updated Content");

        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> response = messageService.updateMessage(1L, updatedMessage);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\":\"Failed to update message\"}", response.getBody());
    }

    @Test
    public void testDeleteMessageSuccess() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(message));
        doNothing().when(messageRepository).deleteById(anyLong());

        ResponseEntity<?> response = messageService.deleteMessage(1L);

        assertNotNull(response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testDeleteMessageNotFound() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> response = messageService.deleteMessage(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\":\"Failed to delete message\"}", response.getBody());
    }

    @Test
    public void testGetMessagesByUserTokenSuccess() {
        when(jwtUtil.extractToken(any())).thenReturn("token");
        when(jwtUtil.extractUserId(any())).thenReturn(1L);
        when(userRepository.findUserById(anyLong())).thenReturn(sender);
        when(messageRepository.findBySenderIdOrReceiverId(anyLong(), anyLong())).thenReturn(Collections.singletonList(message));

        ResponseEntity<List<Message>> response = messageService.getMessagesByUserToken("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(message), response.getBody());
    }

    @Test
    public void testGetMessagesByUserTokenNotFound() {
        when(jwtUtil.extractToken(any())).thenReturn("token");
        when(jwtUtil.extractUserId(any())).thenReturn(1L);
        when(userRepository.findUserById(anyLong())).thenReturn(null);

        ResponseEntity<List<Message>> response = messageService.getMessagesByUserToken("Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

package com.example.projetTechnique.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendResetToken() {
        String email = "test@example.com";
        String resetToken = "abc123";
        emailService.sendResetToken(email, resetToken);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        SimpleMailMessage sentMessage = captureSentMessage();
        assertSentMessageDetails(sentMessage, email, resetToken);
    }

    private SimpleMailMessage captureSentMessage() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        return captor.getValue();
    }

    private void assertSentMessageDetails(SimpleMailMessage message, String expectedTo, String expectedToken) {
        Assertions.assertEquals(expectedTo, message.getTo()[0]);
        Assertions.assertEquals("Password Reset Request", message.getSubject());
        Assertions.assertTrue(message.getText().contains(expectedToken));
    }
}

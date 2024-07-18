package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.Role;
import com.example.projetTechnique.controller.bodies.UserLoginRequest;
import com.example.projetTechnique.controller.responses.AuthResponse;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAll() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals(users, result);
    }

    @Test
    public void testGetUserById_UserFound() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testDeleteUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\":\"User deleted successfully\"}", response.getBody());
    }

    @Test
    public void testUpdateUser_Success() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("John");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Updated John");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        ResponseEntity<?> response = userService.updateUser(updatedUser, userId);

        assertNotEquals(updatedUser, response.getBody());
    }

    @Test
    public void testRegister_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUserName("testUserName");
        user.setFirstName("test1");
        user.setLastName("test2");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        ResponseEntity<?> response = userService.register(user);
        assertNotNull(response.getBody());
    }

    @Test
    public void testForgotPassword_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        ResponseEntity<?> response = userService.forgotPassword(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reset token sent to email", response.getBody());
    }

    @Test
    public void testResetPassword_Success() {
        String resetToken = UUID.randomUUID().toString();
        String newPassword = "newPassword";

        User user = new User();
        user.setResetToken(resetToken);

        when(userRepository.findByResetToken(resetToken)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userService.resetPassword(resetToken, newPassword);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset successful", response.getBody());
    }
}

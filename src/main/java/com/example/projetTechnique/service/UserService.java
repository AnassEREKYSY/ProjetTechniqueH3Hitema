package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.Role;
import com.example.projetTechnique.controller.bodies.UserLoginRequest;
import com.example.projetTechnique.controller.responses.AuthResponse;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public ResponseEntity<?> getUserById(Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found with id: " + userId + "\"}");
        }
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + userId));
    }

    public Long getLoggedInUserId(String token) {
        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    @Transactional
    public ResponseEntity<?> deleteUser(Long userId) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            userRepository.delete(existingUser);
            return ResponseEntity.ok("{\"message\":\"User deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"User deletion failed\"}");
        }

    }

    @Transactional
    public ResponseEntity<?> updateUser(User updatedUser, Long id) {
        updatedUser.setId(id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + updatedUser.getId()));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUserName(updatedUser.getUserName());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(hashedPassword);
        }

        try {
            userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"User update failed\"}");
        }

    }

    @Transactional
    public ResponseEntity<?> register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        try {
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"User registration failed\"}");
        }
    }

    public User findByEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userRepository.save(user);
            emailService.sendResetToken(email, resetToken);
        }
        return ResponseEntity.ok("Reset token sent to email");
    }

    public ResponseEntity<?> resetPassword(String resetToken, String newPassword) {
        User user = userRepository.findByResetToken(resetToken);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            userRepository.save(user);
            return ResponseEntity.ok("Password reset successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
    }

    public ResponseEntity<?> login(UserLoginRequest userLoginRequest){
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();

        User user = this.findByEmailAndPassword(email, password);

        if (user != null) {
            String token = JwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

    }

    public ResponseEntity<?> getProfile(String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!JwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String email = JwtUtil.extractEmail(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        User user = findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(user);
    }

}

package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.Role;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
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

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public Long getLoggedInUserId(String token) {
        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    public void deleteUser(Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        userRepository.delete(existingUser);
    }

    public void updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + updatedUser.getId()));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setUserName(updatedUser.getUserName());

        userRepository.save(existingUser);
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
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

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userRepository.save(user);
            emailService.sendResetToken(email, resetToken);
        }
    }

    public boolean resetPassword(String resetToken, String newPassword) {
        User user = userRepository.findByResetToken(resetToken);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}

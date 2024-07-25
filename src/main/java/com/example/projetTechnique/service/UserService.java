package com.example.projetTechnique.service;

import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.security.JwtTokenProvider;
import com.example.projetTechnique.utilities.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private static final String UPLOAD_DIR = "src/main/resources/static/users";

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private JwtTokenProvider jwtTokenProvider;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public ResponseEntity<?> getAll() {
        List<User> users=userRepository.findAll();
        if(users!=null) {
            return  ResponseEntity.ok(userRepository.findAll());
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User list is empty");
        }
    }

    public ResponseEntity<?> getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user!=null){
            return ResponseEntity.ok(user);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + userId));
    }

    public Long getLoggedInUserId(String token) {
        String jwtToken=jwtTokenProvider.extractToken(token);
        String userEmail= jwtTokenProvider.getUserName(jwtToken);
        User user = userRepository.findByEmail(userEmail);
        return user != null ? user.getId() : null;
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

    @Transactional
    public ResponseEntity<?> deleteUser(Long userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
            User TestUser=userRepository.findById(userId).get();
            if(TestUser!=null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User deletion Failed");
            }else{
                return ResponseEntity.ok("User deleted successfully");
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
        }
    }

    @Transactional
    public ResponseEntity<?> updateUser(User updatedUser, Long id) {
        updatedUser.setId(id);
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            existingUser= Optional.of(updatedUser);
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
                existingUser.get().setPassword(hashedPassword);
            }
            userRepository.save(existingUser.get());
            User TestUser=existingUser.get();
            if(TestUser!=existingUser.get()){
                return ResponseEntity.ok(updatedUser);
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User update failed");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
        }
    }

    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userRepository.save(user);
            emailService.sendResetToken(email, resetToken);
            return ResponseEntity.ok("Reset token sent to email");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
        }
    }

    public ResponseEntity<?> resetPassword(String resetToken, String newPassword) {
        User user = userRepository.findByResetToken(resetToken);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            userRepository.save(user);
            return ResponseEntity.ok("Password reset successful");
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
        }
    }

    public ResponseEntity<?> getProfile(String Token){
        String token= jwtTokenProvider.extractToken(Token);

        if (!jwtTokenProvider.validateToken(token)) {
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

    @Transactional
    public ResponseEntity<?> updateProfile(String Token, User updatedUser) {
        String token= jwtTokenProvider.extractToken(Token);

        if (!JwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String email = JwtUtil.extractEmail(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().isEmpty()) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null && !updatedUser.getLastName().isEmpty()) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            existingUser.setUserName(updatedUser.getUsername());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(hashedPassword);
        }
        userRepository.save(existingUser);
        if(updatedUser==existingUser){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User update failed");
        }else{
            return ResponseEntity.ok(existingUser);
        }
    }


    public ResponseEntity<?> uploadImage(MultipartFile imageFile, String token) {
        String jwtToken = jwtTokenProvider.extractToken(token);
        Long loggedInUserId = getLoggedInUserId(jwtToken);

        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized Or user Not Found");
        }

        User user = userRepository.findUserById(loggedInUserId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (imageFile == null || imageFile.isEmpty() ||
                (!imageFile.getContentType().equals("image/jpeg") &&
                        !imageFile.getContentType().equals("image/jpg") &&
                        !imageFile.getContentType().equals("image/png"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid image file");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uniqueFilename = "user_" + loggedInUserId + "_" + System.currentTimeMillis() + fileExtension;

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imagePath = "/users/" + uniqueFilename;
            user.setImage(imagePath);
            userRepository.save(user);

            return ResponseEntity.ok("Image uploaded successfully , imagePath :" + imagePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload the image");
        }
    }

}

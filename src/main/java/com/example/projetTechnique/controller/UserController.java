package com.example.projetTechnique.controller;

import com.example.projetTechnique.controller.bodies.ForgotPasswordRequest;
import com.example.projetTechnique.controller.bodies.ResetPasswordRequest;
import com.example.projetTechnique.controller.bodies.UserLoginRequest;
import com.example.projetTechnique.controller.responses.AuthResponse;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.service.UserService;
import com.example.projetTechnique.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found with id: " + id + "\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"User registration failed\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("{\"message\":\"User deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"User deletion failed\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            updatedUser.setId(id); // Ensure the ID in the path matches the ID in the body
            User user = userService.updateUser(updatedUser);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"User update failed\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();

        User user = userService.findByEmailAndPassword(email, password);

        if (user != null) {
            String token = JwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
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

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Reset token sent to email");
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = userService.resetPassword(request.getResetToken(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
        }
    }
}

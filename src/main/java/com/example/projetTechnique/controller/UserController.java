package com.example.projetTechnique.controller;

import com.example.projetTechnique.controller.bodies.ForgotPasswordRequest;
import com.example.projetTechnique.controller.bodies.ResetPasswordRequest;
import com.example.projetTechnique.controller.bodies.UserLoginRequest;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/one/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return userService.register(user);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("permitAll")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.updateUser(updatedUser,id);
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        return userService.login(userLoginRequest);
    }

    @GetMapping("/profile")
    @PreAuthorize("permitAll")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        return getProfile(token);
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("permitAll")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.forgotPassword(request.getEmail());
    }

    @PostMapping("/reset-password")
    @PreAuthorize("permitAll")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request.getResetToken(), request.getNewPassword());
    }
}

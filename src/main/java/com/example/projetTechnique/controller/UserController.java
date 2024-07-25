package com.example.projetTechnique.controller;

import com.example.projetTechnique.controller.bodies.ForgotPasswordRequest;
import com.example.projetTechnique.controller.bodies.ResetPasswordRequest;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.updateUser(updatedUser,id);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        return userService.getProfile(token);
    }

    @PutMapping("/Updateprofile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token, @RequestBody User updatedUser) {
        return userService.updateProfile(token, updatedUser);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.forgotPassword(request.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request.getResetToken(), request.getNewPassword());
    }

    @PostMapping("/uploadProfilImage")
    public ResponseEntity<?> uploadProfileImage(@RequestHeader("Authorization") String token,
                                                @RequestParam("imageFile") MultipartFile imageFile) {
        return userService.uploadImage(imageFile, token);
    }
}

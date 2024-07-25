package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.JwtAuthResponse;
import com.example.projetTechnique.model.Login;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody Login login){
        JwtAuthResponse response = authService.loggin(login);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        return new ResponseEntity<>(authService.register(user), HttpStatus.CREATED);
    }

}

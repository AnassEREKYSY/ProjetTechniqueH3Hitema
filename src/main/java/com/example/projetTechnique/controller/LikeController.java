package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Like;
import com.example.projetTechnique.service.LikeService;
import com.example.projetTechnique.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PostMapping("/create/{postId}")
    public ResponseEntity<?> createLike(@RequestHeader("Authorization") String token, @PathVariable("postId") Long postId) {
        return likeService.createLike(token,postId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{likeId}")
    public ResponseEntity<?> deleteLike(@PathVariable("likeId") Long likeId, @RequestHeader("Authorization") String token) throws AccessDeniedException {
        return likeService.deleteLike(token,likeId);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllLikes() {
        return likeService.getAllLikes();
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getLikeById(@PathVariable Long id) {
        return likeService.getLikeById(id);
    }
}

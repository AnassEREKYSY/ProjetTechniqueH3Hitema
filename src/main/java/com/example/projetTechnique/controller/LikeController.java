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
import java.util.List;

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
        try {
            Like createdLike = likeService.createLike(token, postId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User or Post not found\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{likeId}")
    public ResponseEntity<?> deleteLike(@PathVariable("likeId") Long likeId, @RequestHeader("Authorization") String token) {
        try {
            likeService.deleteLike(token, likeId);
            return ResponseEntity.ok("{\"message\":\"Like deleted successfully\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Like or User not found\"}");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"Access Denied\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete like: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllLikes() {
        List<Like> likes = likeService.getAllLikes();
        if (!likes.isEmpty()) {
            return ResponseEntity.ok(likes);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"No likes available\"}");
        }
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getLikeById(@PathVariable Long id) {
        Like like = likeService.getLikeById(id);
        if (like != null) {
            return ResponseEntity.ok(like);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Like not found with id: " + id + "\"}");
        }
    }
}

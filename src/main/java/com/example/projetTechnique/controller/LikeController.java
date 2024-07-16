package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Like;
import com.example.projetTechnique.service.LikeService;
import com.example.projetTechnique.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create/{postId}")
    public ResponseEntity<Like> createLike(@RequestHeader("Authorization") String token, @PathVariable("postId") Long postId) {
        try {
            Like createdLike = likeService.createLike(token, postId);
            return ResponseEntity.ok(createdLike);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/delete/{likeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable("likeId") Long likeId, @RequestHeader("Authorization") String token) {
        try {
            likeService.deleteLike(token, likeId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Like>> getAllLikes() {
        List<Like> likes = likeService.getAllLikes();
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<Like> getLikeById(@PathVariable Long id) {
        Like like = likeService.getLikeById(id);
        if (like != null) {
            return ResponseEntity.ok(like);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

}

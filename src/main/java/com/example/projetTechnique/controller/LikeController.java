package com.example.projetTechnique.controller;

import com.example.projetTechnique.service.LikeService;
import com.example.projetTechnique.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;

    @PostMapping("/create/{postId}")
    public ResponseEntity<?> createLike(@RequestHeader("Authorization") String token, @PathVariable("postId") Long postId) {
        return likeService.createLike(token,postId);
    }

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

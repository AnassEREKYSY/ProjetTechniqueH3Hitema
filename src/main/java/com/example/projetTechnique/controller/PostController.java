package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.service.PostService;
import com.example.projetTechnique.service.UserService;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Post post, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        Long loggedInUserId = userService.getLoggedInUserId(jwtToken);
        if (loggedInUserId != null) {
            User loggedInUser = userService.getUserById(loggedInUserId); // Assuming you have this method
            post.setUser(loggedInUser);
            Post createdPost = postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Unauthorized\"}");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        if (!posts.isEmpty()) {
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"No posts available\"}");
        }
    }

    @DeleteMapping("/delete/{idPost}")
    public ResponseEntity<?> deletePost(@PathVariable("idPost") Long idPost, @RequestHeader("Authorization") String token) {
        try {
            Long userId = userService.getLoggedInUserId(token);
            User user = userService.getUserById(userId);

            postService.deletePost(idPost, user);

            return ResponseEntity.ok("{\"message\":\"Post deleted successfully\"}");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Invalid JWT token: " + e.getMessage() + "\"}");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"Access Denied: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete post: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Post not found with id: " + id + "\"}");
        }
    }

    @PutMapping("/update/{idPost}")
    public ResponseEntity<?> updatePost(@PathVariable("idPost") Long idPost, @RequestBody Post updatedPost) {
        try {
            Post post = postService.updatePost(idPost, updatedPost);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Post with id " + idPost + " not found\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to update post\"}");
        }
    }

}

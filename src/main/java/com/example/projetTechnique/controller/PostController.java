package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.service.PostService;
import com.example.projetTechnique.service.UserService;
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

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        Long loggedInUserId = userService.getLoggedInUserId(jwtToken);
        if (loggedInUserId != null) {
            User loggedInUser = userService.getUserById(loggedInUserId); // Assuming you have this method
            post.setUser(loggedInUser);
            Post createdPost = postService.createPost(post);
            return ResponseEntity.ok(createdPost);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/delete/{idPost}/{idUser}")
    public ResponseEntity<String> deletePost(@PathVariable("idPost") Long idPost, @PathVariable("idUser") Long userId) {
        User user = userService.getUserById(userId);
        try {
            postService.deletePost(idPost, user);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete post: " + e.getMessage());
        }
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/update/{idPost}")
    public ResponseEntity<Post> updatePost(@PathVariable("idPost") Long idPost, @RequestBody Post updatedPost) {
        try {
            Post post = postService.updatePost(idPost, updatedPost);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}

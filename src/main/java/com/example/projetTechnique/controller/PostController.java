package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.service.PostService;
import com.example.projetTechnique.service.UserService;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Post post, @RequestHeader("Authorization") String token) {
        return postService.createPost(post,token);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPosts() {
        return postService.getAllPosts();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{idPost}")
    public ResponseEntity<?> deletePost(@PathVariable("idPost") Long idPost, @RequestHeader("Authorization") String token) throws AccessDeniedException {
        return postService.deletePost(idPost,token);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PutMapping("/update/{idPost}")
    public ResponseEntity<?> updatePost(@PathVariable("idPost") Long idPost, @RequestBody Post updatedPost) {
        return postService.updatePost(idPost,updatedPost);
    }

}

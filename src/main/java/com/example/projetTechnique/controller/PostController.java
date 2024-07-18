package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.service.PostService;
import com.example.projetTechnique.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

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
    public ResponseEntity<?> createPost(@RequestBody String postJson, @RequestHeader("Authorization") String token) throws JsonProcessingException {
        return postService.createPost(new ObjectMapper().readValue(postJson, Post.class), token);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPosts() {
        return postService.getAllPosts();
    }

    @DeleteMapping("/delete/{idPost}")
    public ResponseEntity<?> deletePost(@PathVariable("idPost") Long idPost, @RequestHeader("Authorization") String token) throws AccessDeniedException {
        return postService.deletePost(idPost,token);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }


    @PutMapping("/update/{idPost}")
    public ResponseEntity<?> updatePost(@PathVariable("idPost") Long idPost, @RequestParam("post") String postJson, @RequestParam("image") MultipartFile imageFile) throws JsonProcessingException {
        return postService.updatePost(idPost, new ObjectMapper().readValue(postJson, Post.class), imageFile);
    }


}

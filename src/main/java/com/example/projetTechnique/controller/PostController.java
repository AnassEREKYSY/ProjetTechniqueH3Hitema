package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@AllArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private final PostService postService;


    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody String postJson, @RequestHeader("Authorization") String token) throws JsonProcessingException {
        return postService.createPost(new ObjectMapper().readValue(postJson, Post.class), token);
    }

    @PermitAll
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @DeleteMapping("/delete/{idPost}")
    public ResponseEntity<?> deletePost(@PathVariable("idPost") Long idPost, @RequestHeader("Authorization") String token){
        return postService.deletePost(idPost,token);
    }

    @PutMapping("/update/{idPost}")
    public ResponseEntity<?> updatePost(
            @PathVariable("idPost") Long idPost,
            @RequestParam("post") String postJson,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader("Authorization") String token
    ) throws JsonProcessingException {
        Post post = new ObjectMapper().readValue(postJson, Post.class);
        return postService.updatePost(idPost, post, imageFile, token);
    }

    @PostMapping("/uploadImage/{postId}")
    public ResponseEntity<?> uploadImage(@PathVariable("postId") Long postId, @RequestParam("image") MultipartFile imageFile, @RequestHeader("Authorization") String token) {
        return postService.uploadImage(postId, imageFile, token);
    }


}

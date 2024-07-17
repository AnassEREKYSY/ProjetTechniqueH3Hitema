package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.Role;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.PostRepository;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserService userService;

    public ResponseEntity<?> createPost(Post post , String token) {
        String jwtToken = token.substring(7);
        Long loggedInUserId = userService.getLoggedInUserId(jwtToken);
        if (loggedInUserId != null) {
            User loggedInUser = userService.findUserById(loggedInUserId);
            post.setUser(loggedInUser);
            post.setDateCreation(new Date());
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Unauthorized\"}");
        }
    }

    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        if (!posts.isEmpty()) {
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"No posts available\"}");
        }
    }

    public ResponseEntity<?> getPostById(Long id) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Post not found with id: " + id + "\"}");
        }
    }

    public ResponseEntity<?> deletePost(Long id,String token ) throws AccessDeniedException {
        try {
            Long userId = userService.getLoggedInUserId(token);
            User user = userService.findUserById(userId);

            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
            if (post.getUser() == null) {
                throw new IllegalStateException("Post does not have an associated user");
            }
            if (!post.getUser().equals(user)) {
                throw new AccessDeniedException("You are not authorized to delete this post");
            }
            postRepository.delete(post);

            return ResponseEntity.ok("{\"message\":\"Post deleted successfully\"}");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Invalid JWT token: " + e.getMessage() + "\"}");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"Access Denied: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete post: " + e.getMessage() + "\"}");
        }
    }

    public ResponseEntity<?> updatePost(Long idPost, Post updatedPost) {

        try {
            Optional<Post> optionalPost = postRepository.findById(idPost);
            if (optionalPost.isPresent()) {
                Post existingPost = optionalPost.get();

                if (updatedPost.getImage() != null && !updatedPost.getImage().isEmpty()) {
                    existingPost.setImage(updatedPost.getImage());
                }
                if (updatedPost.getContenu() != null && !updatedPost.getContenu().isEmpty()) {
                    existingPost.setContenu(updatedPost.getContenu());
                }
                if (updatedPost.getDateCreation() != null) {
                    existingPost.setDateCreation(updatedPost.getDateCreation());
                }
                if (updatedPost.getUser() != null) {
                    existingPost.setUser(updatedPost.getUser());
                }

                postRepository.save(existingPost);
                return ResponseEntity.ok(existingPost);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Post with id " + idPost + " not found\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Post with id " + idPost + " not found\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to update post\"}");
        }
    }
}

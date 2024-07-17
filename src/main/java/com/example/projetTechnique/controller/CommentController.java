package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Comment;
import com.example.projetTechnique.service.CommentService;
import com.example.projetTechnique.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PostMapping("/create/{postId}")
    public ResponseEntity<?> createComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("postId") Long postId,
                                           @RequestBody Comment comment) {
        try {
            Comment createdComment = commentService.createComment(token, postId, comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User or Post not found\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to create comment: " + e.getMessage() + "\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("commentId") Long commentId) {
        try {
            commentService.deleteComment(token, commentId);
            return ResponseEntity.ok("{\"message\":\"Comment deleted successfully\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Comment not found\"}");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"Access Denied\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete comment: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        if (!comments.isEmpty()) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"No comments available\"}");
        }
    }

    @GetMapping("/one/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Comment not found with id: " + commentId + "\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasRole('ROLE_USER') and #userId == authentication.principal.id")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@RequestHeader(name = "Authorization") String token,
                                           @PathVariable Long id,
                                           @RequestBody Comment updatedComment) {
        try {
            Long loggedInUserId = jwtUtil.extractUserId(token);
            if (loggedInUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Unauthorized\"}");
            }
            Comment comment = commentService.updateComment(id, loggedInUserId, updatedComment);
            if (comment != null) {
                return ResponseEntity.ok(comment);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Comment not found with id: " + id + "\"}");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"User is not authorized to update this comment\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to update comment: " + e.getMessage() + "\"}");
        }
    }
}

package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Comment;
import com.example.projetTechnique.service.CommentService;
import com.example.projetTechnique.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/create/{postId}")
    public ResponseEntity<Comment> createComment(@RequestHeader("Authorization") String token,
                                                 @PathVariable("postId") Long postId,
                                                 @RequestBody Comment comment) {
        try {
            Comment createdComment = commentService.createComment(token, postId, comment);
            return ResponseEntity.ok(createdComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestHeader("Authorization") String token,
                                              @PathVariable("commentId") Long commentId) {
        try {
            commentService.deleteComment(token, commentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/one/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Comment> updateComment(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id,
            @RequestBody Comment updatedComment) {

        Long loggedInUserId = jwtUtil.extractUserId(token); // Extract user ID from token

        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Or handle as per your application's authentication logic
        }

        Comment comment = commentService.updateComment(id, loggedInUserId, updatedComment);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(comment);
    }

}

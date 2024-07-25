package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Comment;
import com.example.projetTechnique.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/create/{postId}")
    public ResponseEntity<?> createComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("postId") Long postId,
                                           @RequestBody Comment comment) {
        return commentService.createComment(token, postId, comment);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("commentId") Long commentId) {
        return commentService.deleteComment(token, commentId);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/one/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId) {
        return  commentService.getCommentById(commentId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@RequestHeader(name = "Authorization") String token,
                                           @PathVariable Long id,
                                           @RequestBody Comment updatedComment) {

            return commentService.updateComment(id, token, updatedComment);
    }
}

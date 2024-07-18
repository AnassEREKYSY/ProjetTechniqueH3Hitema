package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Comment;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.CommentRepository;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    NotificationService notificationService;

    public ResponseEntity<?> createComment(String token, Long postId, Comment comment) {

        try {
            Long userId = userService.getLoggedInUserId(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
            Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

            comment.setUser(user);
            comment.setPost(post);
            comment.setDateComment(new Date());
            commentRepository.save(comment);
            String message = "User " + user.getUserName() + " liked your post.";
            notificationService.createNotification(post.getUser().getId(), postId, message, NotificationType.COMMENT);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User or Post not found\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to create comment: " + e.getMessage() + "\"}");
        }
    }

    public ResponseEntity<?> deleteComment(String token, Long commentId) {
            Long userId = userService.getLoggedInUserId(token);
            Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));

            if (!comment.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete comment");
            }
            commentRepository.delete(comment);
            if(commentRepository.findById(commentId)==null){
                return ResponseEntity.ok("{\"message\":\"Comment deleted successfully\"}");
            }
            else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete comment");
            }
    }

    public ResponseEntity<?> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        if (!comments.isEmpty()) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"No comments available\"}");
        }
    }

    public ResponseEntity<?> getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Comment not found with id: " + commentId + "\"}");
        }
    }

    public ResponseEntity<?> updateComment(Long id, String token, Comment updatedComment) {

        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Unauthorized\"}");
        }
        Optional<Comment> optionalComment = commentRepository.findById(id);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();

            if (!comment.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Unauthorized\"}");
            }

            comment.setContenu(updatedComment.getContenu());
            commentRepository.save(comment);
            return ResponseEntity.ok(updatedComment);
        }
        return null;
    }
}

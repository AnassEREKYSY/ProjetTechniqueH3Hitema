package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Comment;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.CommentRepository;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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

    public Comment createComment(String token, Long postId, Comment comment) {
        Long userId = userService.getLoggedInUserId(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        comment.setUser(user);
        comment.setPost(post);
        comment.setDateComment(new Date());

        return commentRepository.save(comment);
    }

    public void deleteComment(String token, Long commentId) throws AccessDeniedException {
        Long userId = userService.getLoggedInUserId(token);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public Comment updateComment(Long id, Long userId, Comment updatedComment) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();

            if (!comment.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("User is not authorized to update this comment");
            }

            comment.setContenu(updatedComment.getContenu());
            return commentRepository.save(comment);
        }
        return null;
    }

}

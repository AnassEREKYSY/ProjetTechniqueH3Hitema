package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Comment;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.CommentRepository;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateComment_Success() {
        String token = "valid_token";
        Long userId = 1L;
        Long postId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUserName("testuser");

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);
        comment.setPost(post);
        comment.setDateComment(new Date());
        comment.setContenu("Test comment");

        when(userService.getLoggedInUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        ResponseEntity<?> response = commentService.createComment(token, postId, comment);

        assertNotEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User or Post not found",response.getBody().toString());
    }

    @Test
    public void testDeleteComment_Success() {
        String token = "valid_token";
        Long commentId = 1L;
        Long userId = 1L;

        Comment comment = new Comment();
        comment.setId(commentId);
        User user = new User();
        user.setId(userId);
        comment.setUser(user);

        when(userService.getLoggedInUserId(token)).thenReturn(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ResponseEntity<?> response = commentService.deleteComment(token, commentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\":\"Failed to delete comment", response.getBody());
    }

    @Test
    public void testGetAllComments_NotEmpty() {
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.findAll()).thenReturn(comments);

        ResponseEntity<?> response = commentService.getAllComments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    public void testGetCommentById_Success() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ResponseEntity<?> response = commentService.getCommentById(commentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    public void testUpdateComment_Success() {
        Long commentId = 1L;
        String token = "valid_token";
        Long userId = 1L;

        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setContenu("Old content");
        User user = new User();
        user.setId(userId);
        existingComment.setUser(user);

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setContenu("Updated content");

        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(existingComment)).thenReturn(existingComment);

        ResponseEntity<?> response = commentService.updateComment(commentId, token, updatedComment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedComment, response.getBody());
    }
}

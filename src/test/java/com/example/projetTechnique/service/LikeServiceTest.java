package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Like;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.LikeRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testCreateLike_Success() {
        String bearerToken = "Bearer valid_token";
        String token = "valid_token";
        Long userId = 1L;
        Long postId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUserName("testUser");

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        when(jwtUtil.extractToken(bearerToken)).thenReturn(token);
        when(userService.getLoggedInUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        ResponseEntity<?> response = likeService.createLike(bearerToken, postId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotEquals(like, response.getBody());
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(notificationService, times(1)).createNotification(post.getUser().getId(), postId, "User testUser liked your post.", NotificationType.LIKE);
    }

    @Test
    public void testCreateLike_UserAlreadyLiked() {
        String bearerToken = "Bearer valid_token";
        String token = "valid_token";
        Long userId = 1L;
        Long postId = 1L;

        User user = new User();
        user.setId(userId);

        Post post = new Post();
        post.setId(postId);

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        when(jwtUtil.extractToken(bearerToken)).thenReturn(token);
        when(userService.getLoggedInUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.of(like));

        ResponseEntity<?> response = likeService.createLike(bearerToken, postId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(likeRepository, never()).save(any(Like.class));
        verify(notificationService, never()).createNotification(anyLong(), anyLong(), anyString(), any(NotificationType.class));
    }

    @Test
    public void testDeleteLike_Success() {
        String token = "valid_token";
        Long userId = 1L;
        Long likeId = 1L;

        User user = new User();
        user.setId(userId);

        Like like = new Like();
        like.setId(likeId);
        like.setUser(user);

        when(userService.getLoggedInUserId(token)).thenReturn(userId);
        when(likeRepository.findById(likeId)).thenReturn(Optional.of(like));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = likeService.deleteLike(token, likeId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to delete like", response.getBody());
    }

    @Test
    public void testGetAllLikes_NotEmpty() {
        Like like1 = new Like();
        Like like2 = new Like();
        List<Like> likes = Arrays.asList(like1, like2);

        when(likeRepository.findAll()).thenReturn(likes);

        ResponseEntity<?> response = likeService.getAllLikes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(likes, response.getBody());
    }

    @Test
    public void testGetAllLikes_Empty() {
        when(likeRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = likeService.getAllLikes();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("{\"message\":\"No likes available\"}", response.getBody());
    }
}

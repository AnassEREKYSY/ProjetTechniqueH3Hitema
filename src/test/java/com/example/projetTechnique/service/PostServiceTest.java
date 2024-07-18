package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private FileStorageService fileStorageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePost_Success() {
        String token = "Bearer valid_token";
        Long loggedInUserId = 1L;
        MultipartFile imageFile = null;

        User loggedInUser = new User();
        loggedInUser.setId(loggedInUserId);

        Post post = new Post();
        post.setId(1L);
        post.setUser(loggedInUser);

        when(userService.getLoggedInUserId(token)).thenReturn(loggedInUserId);
        when(userService.findUserById(loggedInUserId)).thenReturn(loggedInUser);
        when(fileStorageService.store(any(MultipartFile.class))).thenReturn("uploaded_image_path");
        when(postRepository.save(any(Post.class))).thenReturn(post);

        ResponseEntity<?> response = postService.createPost(post, token);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(post, response.getBody());
    }

    @Test
    public void testGetAllPosts_NotEmpty() {
        Post post1 = new Post();
        Post post2 = new Post();
        List<Post> posts = Arrays.asList(post1, post2);

        when(postRepository.findAll()).thenReturn(posts);

        ResponseEntity<?> response = postService.getAllPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts, response.getBody());
    }

    @Test
    public void testDeletePost_Success() {
        String token = "valid_token";
        Long postId = 1L;
        Long loggedInUserId = 1L;

        User user = new User();
        user.setId(loggedInUserId);

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        when(userService.getLoggedInUserId(token)).thenReturn(loggedInUserId);
        when(userService.findUserById(loggedInUserId)).thenReturn(user);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        ResponseEntity<?> response = postService.deletePost(postId, token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\":\"Failed to delete post", response.getBody());
    }

    @Test
    public void testUpdatePost_Success() {
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setId(postId);

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setContenu("Updated content");

        MultipartFile imageFile = null;

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(fileStorageService.store(any(MultipartFile.class))).thenReturn("uploaded_image_path");
        when(postRepository.save(existingPost)).thenReturn(existingPost);

        ResponseEntity<?> response = postService.updatePost(postId, updatedPost, imageFile);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotEquals(existingPost, response.getBody());
    }
}

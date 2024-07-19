package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Bookmark;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.BookmarkRepository;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceTest {

    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private JwtUtil jwtUtil;

    private User user;
    private Post post;
    private Bookmark bookmark;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        post = new Post();
        post.setId(1L);
        bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setPost(post);
    }

    @Test
    public void testAddBookmarkSuccess() {
        when(jwtUtil.extractToken(any())).thenReturn("token");
        when(jwtUtil.extractUserId(any())).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);

        ResponseEntity<?> response = bookmarkService.addBookmark("token", 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    public void testGetBookmarksByUserSuccess() {
        when(jwtUtil.extractUserId(any())).thenReturn(1L);
        when(bookmarkRepository.findByUserId(anyLong())).thenReturn(Collections.singletonList(bookmark));

        ResponseEntity<?> response = bookmarkService.getBookmarksByUser("token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(bookmark), response.getBody());
    }

    @Test
    public void testUpdateBookmarkSuccess() {
        when(jwtUtil.extractToken(any())).thenReturn("token");
        when(jwtUtil.extractUserId(any())).thenReturn(1L);
        when(bookmarkRepository.findById(anyLong())).thenReturn(Optional.of(bookmark));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);

        ResponseEntity<?> response = bookmarkService.updateBookmark(1L, 2L, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookmark, response.getBody());
    }

    @Test
    public void testUpdateBookmarkUnauthorized() {
        when(jwtUtil.extractToken(any())).thenReturn("token");
        when(jwtUtil.extractUserId(any())).thenReturn(2L);
        when(bookmarkRepository.findById(anyLong())).thenReturn(Optional.of(bookmark));

        ResponseEntity<?> response = bookmarkService.updateBookmark(1L, 2L, "token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}

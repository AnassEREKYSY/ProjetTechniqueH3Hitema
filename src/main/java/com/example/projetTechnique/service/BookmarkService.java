package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Bookmark;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.BookmarkRepository;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.utilities.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

     @Autowired
     private JwtUtil jwtUtil;

    public ResponseEntity<?> addBookmark(String token, Long postId) {
        String validToken=jwtUtil.extractToken(token);
        Long userId=jwtUtil.extractUserId(validToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setPost(post);

        bookmarkRepository.save(bookmark);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmark);
    }

    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }

    public ResponseEntity<?> getBookmarksByUser(String token) {
        Long userId=jwtUtil.extractUserId(token);
        List<Bookmark> bookmarks =bookmarkRepository.findByUserId(userId);
        return ResponseEntity.ok(bookmarks);
    }

    public ResponseEntity<?> updateBookmark(Long bookmarkId, Long newPostId, String token) {
        String validToken=jwtUtil.extractToken(token);
        Long userId=jwtUtil.extractUserId(validToken);

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        if(bookmark.getUser().getId()!=userId){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Could not update bookmark\"}");
        }

        Post newPost = postRepository.findById(newPostId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        bookmark.setPost(newPost);
        bookmarkRepository.save(bookmark);
        return ResponseEntity.ok(bookmark);
    }
}


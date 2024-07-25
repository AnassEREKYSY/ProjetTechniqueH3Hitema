package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Bookmark;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.BookmarkRepository;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.security.JwtTokenProvider;
import com.example.projetTechnique.utilities.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
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

     @Autowired
     private JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<?> addBookmark(String token, Long postId) {
        String validToken=jwtTokenProvider.extractToken(token);
        Long userId=jwtUtil.extractUserId(validToken);
        Optional<User> user = userRepository.findById(userId);
        Optional<Post> post = postRepository.findById(postId);
        if (user.isPresent() && post.isPresent()) {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user.get());
            bookmark.setPost(post.get());

            bookmarkRepository.save(bookmark);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookmark);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
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
        String validToken=jwtTokenProvider.extractToken(token);
        Long userId=jwtUtil.extractUserId(validToken);

        Optional<Bookmark> bookmark = bookmarkRepository.findById(bookmarkId);

        if(bookmark.get().getUser().getId()!=userId){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not update bookmark User Unauthorized");
        }
        Optional<Post> newPost = postRepository.findById(newPostId);
        if(newPost.isPresent()){
            bookmark.get().setPost(newPost.get());
            bookmarkRepository.save(bookmark.get());
            return ResponseEntity.ok(bookmark);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Post Not Found");
    }
}


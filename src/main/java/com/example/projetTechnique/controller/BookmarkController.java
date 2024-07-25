package com.example.projetTechnique.controller;

import com.example.projetTechnique.model.Bookmark;
import com.example.projetTechnique.service.BookmarkService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/create/{postId}")
    public ResponseEntity<?> addBookmark(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        return bookmarkService.addBookmark(token, postId);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Bookmark>> getAllBookmarks() {
        return ResponseEntity.ok(bookmarkService.getAllBookmarks());
    }

    @GetMapping("/getUserBookmarks")
    public ResponseEntity<?> getBookmarksByUser(@RequestHeader("Authorization") String token) {
        return bookmarkService.getBookmarksByUser(token);
    }

    @PutMapping("/updateBookmark/{bookmarkId}/{newPostId}")
    public ResponseEntity<?> updateBookmark(@PathVariable("bookmarkId") Long bookmarkId, @PathVariable("newPostId") Long newPostId,@RequestHeader("Authorization") String token) {
        return bookmarkService.updateBookmark(bookmarkId, newPostId, token);
    }
}

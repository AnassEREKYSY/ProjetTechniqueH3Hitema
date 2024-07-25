package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.security.JwtTokenProvider;
import com.example.projetTechnique.utilities.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String UPLOAD_DIR = "src/main/resources/static/posts";

    public ResponseEntity<?> createPost(Post post, String token) {
        String jwtToken = jwtTokenProvider.extractToken(token);
        Long loggedInUserId = userService.getLoggedInUserId(jwtToken);
        if (loggedInUserId != null) {
            User loggedInUser = userService.findUserById(loggedInUserId);
            post.setUser(loggedInUser);
            post.setDateCreation(new Date());
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faild to create Post");
        }
    }

    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        if (!posts.isEmpty()) {
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No posts available");
        }
    }

    public ResponseEntity<?> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return ResponseEntity.ok(post);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }

    public ResponseEntity<?> deletePost(Long id,String token ) {
        Long userId = userService.getLoggedInUserId(token);
        User user = userService.findUserById(userId);
        Optional<Post> post = postRepository.findById(id);
        if (post.get().getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Post Deletion failed");
        }
        if (!post.get().getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User Unauthorized");
        }
        postRepository.delete(post.get());
        Post TestPost=postRepository.findById(id).get();
        if(TestPost==null){
            return ResponseEntity.ok("Post deleted successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete post");
        }
    }

    public ResponseEntity<?> uploadImage(Long postId, MultipartFile imageFile, String token) {
        String jwtToken = jwtTokenProvider.extractToken(token);
        Long loggedInUserId = userService.getLoggedInUserId(jwtToken);
        if (loggedInUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized Or user Not Found");
        }
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        Post post = optionalPost.get();
        if (!post.getUser().getId().equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to upload an image for this post");
        }

        if (imageFile == null || imageFile.isEmpty() ||
                (!imageFile.getContentType().equals("image/jpeg") &&
                        !imageFile.getContentType().equals("image/jpg") &&
                        !imageFile.getContentType().equals("image/png"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid image file");
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uniqueFilename = "post_" + postId + "_" + System.currentTimeMillis() + fileExtension;

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imagePath = "posts/" + uniqueFilename;
            post.setImage(imagePath);
            postRepository.save(post);

            return ResponseEntity.ok("Image uploaded successfully , imagePath:" + imagePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload the image");
        }
    }

    public ResponseEntity<?> updatePost(Long idPost, Post updatedPost, MultipartFile imageFile,String token) {
        String jwtToken=jwtTokenProvider.extractToken(token);
        if(!jwtUtil.validateToken(jwtToken)){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unvalid token");
        }
        Long userID=jwtUtil.extractUserId(jwtToken);
        Optional<Post> optionalPost = postRepository.findById(idPost);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();
            Post TestPost=existingPost;
            if(userID!=existingPost.getUser().getId()){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User Unauthorized");
            }
            if (updatedPost.getImage() != null && !updatedPost.getImage().isEmpty()) {
                    existingPost.setImage(updatedPost.getImage());
            }
            if (updatedPost.getContenu() != null && !updatedPost.getContenu().isEmpty()) {
                    existingPost.setContenu(updatedPost.getContenu());
            }
            if (updatedPost.getDateCreation() != null) {
                    existingPost.setDateCreation(updatedPost.getDateCreation());
            }
            if (updatedPost.getUser() != null) {
                    existingPost.setUser(updatedPost.getUser());
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                    String imagePath = fileStorageService.store(imageFile);
                    existingPost.setImage(imagePath);
            }
            postRepository.save(existingPost);
            if(existingPost!=TestPost){
                return ResponseEntity.ok(existingPost);
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update post");
    }
}

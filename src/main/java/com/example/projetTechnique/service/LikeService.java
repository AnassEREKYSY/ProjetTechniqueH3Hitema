package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.NotificationType;
import com.example.projetTechnique.model.Like;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.LikeRepository;
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
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    JwtUtil jwtUtil;

    public ResponseEntity<?> createLike(String bearerToken, Long postId) {
        try {
            String token = jwtUtil.extractToken(bearerToken); // Extract the token
            Long userId = userService.getLoggedInUserId(token);
            Optional<User> userOptional = userRepository.findById(userId);
            Optional<Post> postOptional = postRepository.findById(postId);

            if (userOptional.isPresent() && postOptional.isPresent()) {
                User user = userOptional.get();
                Post post = postOptional.get();
                Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(user.getId(), post.getId());
                if (existingLike.isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\":\"User has already liked this post\"}");
                }
                Like like = new Like();
                like.setUser(user);
                like.setPost(post);
                like.setDateLike(new Date());
                likeRepository.save(like);
                String message = "User " + user.getUserName() + " liked your post.";
                notificationService.createNotification(post.getUser().getId(), postId, message,NotificationType.LIKE);

                return ResponseEntity.status(HttpStatus.CREATED).body(like);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User or Post not found\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User or Post not found\"}");
        }
    }

    public ResponseEntity<?> deleteLike(String token, Long likeId){
            String jwtToken = token.substring(7);
            Long userId = userService.getLoggedInUserId(jwtToken);
            Optional<Like> likeOptional = likeRepository.findById(likeId);
            Optional<User> userOptional = userRepository.findById(userId);
            if (likeOptional.isPresent() && userOptional.isPresent()) {
                Like like = likeOptional.get();
                if (like.getUser().equals(userOptional.get())) {
                    likeRepository.deleteById(likeId);
                    //Like TestLike=likeRepository.findById(likeId).get();
                    //if(TestLike==null){
                        return ResponseEntity.ok("{\"message\":\"Like deleted successfully\"}");
                    //}
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Failed to delete like\"}");
    }

    public ResponseEntity<?> getAllLikes() {

        List<Like> likes =likeRepository.findAll();
        if (!likes.isEmpty()) {
            return ResponseEntity.ok(likes);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{\"message\":\"No likes available\"}");
        }
    }

    public ResponseEntity<?> getLikeById(Long id) {

        Like like = likeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Like not found with id: " + id));
        if (like != null) {
            return ResponseEntity.ok(like);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Like not found with id: " + id + "\"}");
        }
    }

}
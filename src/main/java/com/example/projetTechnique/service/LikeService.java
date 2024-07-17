package com.example.projetTechnique.service;

import com.example.projetTechnique.model.Like;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.LikeRepository;
import com.example.projetTechnique.repository.PostRepository;
import com.example.projetTechnique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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

    public Like createLike(String token, Long postId) {
        Long userId = userService.getLoggedInUserId(token);
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Post> postOptional = postRepository.findById(postId);

        if (userOptional.isPresent() && postOptional.isPresent()) {
            Like like = new Like();
            like.setUser(userOptional.get());
            like.setPost(postOptional.get());
            like.setDateLike(new Date());
            return likeRepository.save(like);
        } else {
            throw new IllegalArgumentException("User or Post not found");
        }
    }

    public void deleteLike(String token, Long likeId) throws AccessDeniedException {
        Long userId = userService.getLoggedInUserId(token);
        Optional<Like> likeOptional = likeRepository.findById(likeId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (likeOptional.isPresent() && userOptional.isPresent()) {
            Like like = likeOptional.get();
            if (like.getUser().equals(userOptional.get())) {
                likeRepository.deleteById(likeId);
            } else {
                throw new AccessDeniedException("User is not authorized to delete this like");
            }
        } else {
            throw new IllegalArgumentException("Like or User not found");
        }
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public Like getLikeById(Long id) {
        return likeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Like not found with id: " + id));
    }

}
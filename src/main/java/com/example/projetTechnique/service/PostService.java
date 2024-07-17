package com.example.projetTechnique.service;

import com.example.projetTechnique.Enum.Role;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        post.setDateCreation(new Date());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }

    public void deletePost(Long id, User user) throws AccessDeniedException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        if (post.getUser() == null) {
            throw new IllegalStateException("Post does not have an associated user");
        }
        if (!post.getUser().equals(user)) {
            throw new AccessDeniedException("You are not authorized to delete this post");
        }
        postRepository.delete(post);
    }

    public Post updatePost(Long idPost, Post updatedPost) {
        Optional<Post> optionalPost = postRepository.findById(idPost);

        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

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

            return postRepository.save(existingPost);
        } else {
            throw new IllegalArgumentException("Post with id " + idPost + " not found");
        }
    }
}

package com.example.projetTechnique.repository;

import com.example.projetTechnique.model.Like;
import com.example.projetTechnique.model.Post;
import com.example.projetTechnique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
}

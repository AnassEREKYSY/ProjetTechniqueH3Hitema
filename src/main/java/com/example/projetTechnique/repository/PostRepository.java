package com.example.projetTechnique.repository;

import com.example.projetTechnique.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}

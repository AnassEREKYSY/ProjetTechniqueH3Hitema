package com.example.projetTechnique.repository;

import com.example.projetTechnique.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

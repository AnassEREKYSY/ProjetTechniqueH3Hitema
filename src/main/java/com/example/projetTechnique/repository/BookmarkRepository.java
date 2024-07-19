package com.example.projetTechnique.repository;

import com.example.projetTechnique.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUserId(Long userId);

    List<Bookmark> findByPostId(Long postId);
}
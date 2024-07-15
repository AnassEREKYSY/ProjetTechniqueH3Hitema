package com.example.projetTechnique.repository;

import com.example.projetTechnique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserName(String userName);
    User findByResetToken(String resetToken);
    User findByEmailAndPassword(String email, String hashedPassword);
}

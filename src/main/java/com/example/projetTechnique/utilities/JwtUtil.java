package com.example.projetTechnique.utilities;

import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

    @Value("${application.security.jwt.secret-key}")
    private static String jwtSecret;

    @Autowired
    UserRepository userRepository;

    public static Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            return null;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        String email = claims != null ? claims.getSubject() : null;
        if (email != null) {
            User user = this.userRepository.findByEmail(email);
            if (user != null) {
                return user.getId();
            }
        }
        return null;
    }

    public static String extractEmail(String token) {
        Claims claims = extractClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public static boolean validateToken(String token) {
        return extractClaims(token) != null;
    }

    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        else{
            return bearerToken;
        }
    }
}

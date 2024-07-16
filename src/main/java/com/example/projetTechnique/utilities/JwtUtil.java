package com.example.projetTechnique.utilities;

import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your_secret_key";
    private static final long EXPIRATION_TIME = 864_000_000;

    @Autowired
    UserRepository userRepository;

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public static Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            // Handle invalid signature
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
}

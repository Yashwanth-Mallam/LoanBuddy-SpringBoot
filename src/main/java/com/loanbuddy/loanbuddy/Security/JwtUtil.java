package com.loanbuddy.loanbuddy.Security;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.loanbuddy.loanbuddy.Exceptions.ResourceNotFoundException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    private SecretKey key;

    // ✅ Inject secret key from application.properties
    public JwtUtil(@Value("${spring.jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // ✅ Validate the key after dependency injection
    @PostConstruct
    public void validateKey() {
        if (key == null) {
            throw new IllegalStateException("JWT secret key is not initialized!");
        }
    }

    // ✅ Generate JWT Token
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())   
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // ✅ Extract Username (Handled by Global Exception Handler)
    public Optional<String> extractUsername(String token) {
        return Optional.ofNullable(parseClaims(token).getSubject());
    }

    // ✅ Validate Token (Handled by Global Exception Handler)
    public boolean validateToken(String token, String username) {
        try {
            System.out.println("Received Token: " + token); // Debugging Line
            
            Claims claims = Jwts.parser()
                .verifyWith(key) // Verify signature with secret key
                .build()
                .parseSignedClaims(token) // Will throw if invalid
                .getPayload();
    
            return claims.getSubject().equals(username) && !isTokenExpired(claims);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Token validation failed: " + e.getMessage());
        }
    }
    

    // ✅ Check Token Expiration
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // ✅ Common Method to Parse Claims
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    

    
}
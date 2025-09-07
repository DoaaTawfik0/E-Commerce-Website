package com.learningSpringBoot.E_Commerce.Spring.Boot.application.utilities;

import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.InvalidTokenException;
import com.learningSpringBoot.E_Commerce.Spring.Boot.application.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
public class JWTUtility {

    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION_TIME = Duration.ofHours(1).toMillis();
    private SecretKey key;

    @PostConstruct
    public void init() {
        validateSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Secret initialized successfully");
    }

    private void validateSecret() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException(
                    "JWT secret is not configured. " +
                            "Please set the jwt.secret property in application.properties"
            );
        }

        if (secret.length() < 32) {
            log.warn("JWT secret is shorter than recommended 32 characters");
        }
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username, UserDetails userDetails) {
        try {
            boolean isSameUser = username.equals(userDetails.getUsername());
            boolean isNotExpired = !isTokenExpired(token);
            return isSameUser && isNotExpired;
        } catch (TokenExpiredException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidTokenException();
        }
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            log.debug("Token expired: {}", ex.getMessage());
            throw new TokenExpiredException();
        } catch (Exception ex) {
            log.warn("Invalid token: {}", ex.getMessage());
            throw new InvalidTokenException();
        }
    }

}
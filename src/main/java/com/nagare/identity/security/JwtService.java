package com.nagare.identity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Access token 15 phut, mang theo tokenVersion de so voi user.tokenVersion hien tai -
 * neu lech thi token bi coi la vo hieu ngay lap tuc (khoa tai khoan co hieu luc ngay, khong doi 15 phut).
 */
@Service
public class JwtService {

    private final SecretKey key;
    private static final long ACCESS_TTL_MS = 15 * 60 * 1000L; // 15 phut

    public JwtService(@Value("${app.jwt-secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId, String username, String role, long tokenVersion) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .claim("tv", tokenVersion)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ACCESS_TTL_MS)))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getAccessTtlSeconds() {
        return ACCESS_TTL_MS / 1000;
    }
}

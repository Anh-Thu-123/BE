package com.nagare.common.storage;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * URL ky han cho tep nhay cam (ho so visa) - thay the "privateDownload" cua Cloudinary.
 * Token la mot JWT rieng, chi mang claim fileId, het han sau 15 phut nhu thiet ke goc yeu cau.
 */
@Service
public class FileAccessTokenService {

    private static final long TTL_MINUTES = 15;
    private final SecretKey key;

    public FileAccessTokenService(@Value("${app.jwt-secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String issue(String fileId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(fileId)
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(now.plus(TTL_MINUTES, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /** Tra ve fileId neu token con hop le, nem loi neu het han/gia mao. */
    public String verify(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}

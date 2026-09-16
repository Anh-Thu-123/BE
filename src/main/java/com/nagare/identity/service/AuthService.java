package com.nagare.identity.service;

import com.nagare.common.error.ApiException;
import com.nagare.identity.dto.AuthDtos.*;
import com.nagare.identity.model.RefreshToken;
import com.nagare.identity.model.Role;
import com.nagare.identity.model.User;
import com.nagare.identity.model.UserStatus;
import com.nagare.identity.repo.RefreshTokenRepository;
import com.nagare.identity.repo.UserRepository;
import com.nagare.identity.security.JwtService;
import com.nagare.identity.security.UserPrincipal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final long REFRESH_TTL_DAYS = 14;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Khach tu dang ky: chi username + password, vai tro CUSTOMER tu dong, khong xac thuc gi them. */
    public User register(RegisterRequest req) {
        String username = req.username().toLowerCase().trim();
        if (userRepository.existsByUsername(username)) {
            throw ApiException.conflict("USERNAME_TAKEN", "Ten dang nhap da ton tai");
        }
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(Role.CUSTOMER);
        u.setStatus(UserStatus.ACTIVE);
        u.setMustChangePassword(false);
        return userRepository.save(u);
    }

    public TokenResponse login(LoginRequest req) {
        User u = userRepository.findByUsername(req.username().toLowerCase().trim())
                .orElseThrow(() -> ApiException.unauthorized("Sai ten dang nhap hoac mat khau"));
        if (u.getStatus() == UserStatus.LOCKED) {
            throw ApiException.unauthorized("Tai khoan da bi khoa");
        }
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw ApiException.unauthorized("Sai ten dang nhap hoac mat khau");
        }
        u.setLastLoginAt(Instant.now());
        userRepository.save(u);
        return issueTokens(u);
    }

    public TokenResponse refresh(String refreshTokenRaw) {
        String hash = hash(refreshTokenRaw);
        RefreshToken rt = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> ApiException.unauthorized("Refresh token khong hop le"));
        if (rt.getRevokedAt() != null || rt.getExpiresAt().isBefore(Instant.now())) {
            throw ApiException.unauthorized("Refresh token het han hoac da bi thu hoi");
        }
        User u = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> ApiException.unauthorized("Tai khoan khong ton tai"));
        if (u.getStatus() == UserStatus.LOCKED) {
            throw ApiException.unauthorized("Tai khoan da bi khoa");
        }
        // xoay refresh token: thu hoi cai cu, cap cai moi
        rt.setRevokedAt(Instant.now());
        refreshTokenRepository.save(rt);
        return issueTokens(u);
    }

    public void logout(String refreshTokenRaw) {
        if (refreshTokenRaw == null) return;
        refreshTokenRepository.findByTokenHash(hash(refreshTokenRaw)).ifPresent(rt -> {
            rt.setRevokedAt(Instant.now());
            refreshTokenRepository.save(rt);
        });
    }

    public void changePassword(String userId, ChangePasswordRequest req) {
        User u = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("Tai khoan"));
        if (!passwordEncoder.matches(req.currentPassword(), u.getPasswordHash())) {
            throw ApiException.badRequest("BAD_CURRENT_PASSWORD", "Mat khau hien tai khong dung");
        }
        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        u.setMustChangePassword(false);
        u.setTokenVersion(u.getTokenVersion() + 1); // vo hieu moi token cu, bat dang nhap lai bang mat khau moi
        userRepository.save(u);
    }

    /** Khoa/mo khoa tai khoan phai co hieu luc ngay: tang tokenVersion. */
    public void setStatus(String userId, UserStatus status) {
        User u = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("Tai khoan"));
        u.setStatus(status);
        u.setTokenVersion(u.getTokenVersion() + 1);
        userRepository.save(u);
        if (status == UserStatus.LOCKED) {
            refreshTokenRepository.deleteByUserId(userId);
        }
    }

    private TokenResponse issueTokens(User u) {
        String access = jwtService.generateAccessToken(u.getId(), u.getUsername(), u.getRole().name(), u.getTokenVersion());
        String refreshRaw = generateRandomToken();
        RefreshToken rt = new RefreshToken();
        rt.setUserId(u.getId());
        rt.setTokenHash(hash(refreshRaw));
        rt.setExpiresAt(Instant.now().plus(REFRESH_TTL_DAYS, ChronoUnit.DAYS));
        refreshTokenRepository.save(rt);
        return new TokenResponse(access, refreshRaw, jwtService.getAccessTtlSeconds());
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String raw) {
        // Refresh token la chuoi ngau nhien 48 byte (khong phai mat khau nguoi dung nghi ra),
        // nen bam SHA-256 tat dinh de tra cuu truc tiep theo tokenHash la an toan va du nhanh.
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] out = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(out);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

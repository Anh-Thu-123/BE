package com.nagare.identity.web;

import com.nagare.common.error.ApiException;
import com.nagare.identity.dto.AuthDtos.*;
import com.nagare.identity.model.User;
import com.nagare.identity.repo.UserRepository;
import com.nagare.identity.security.SecurityUtils;
import com.nagare.identity.security.UserPrincipal;
import com.nagare.identity.service.AuthService;
import com.nagare.identity.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RateLimiterService rateLimiterService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, RateLimiterService rateLimiterService, UserRepository userRepository) {
        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req, HttpServletRequest request) {
        String ip = clientIp(request);
        if (!rateLimiterService.tryRegister(ip)) {
            throw ApiException.conflict("RATE_LIMITED", "Qua nhieu lan dang ky, thu lai sau");
        }
        authService.register(req);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return authService.refresh(req.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshRequest req) {
        authService.logout(req != null ? req.refreshToken() : null);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public MeResponse me() {
        UserPrincipal p = SecurityUtils.currentUser();
        User u = userRepository.findById(p.getId()).orElseThrow(() -> ApiException.notFound("Tai khoan"));
        return new MeResponse(u.getId(), u.getUsername(), u.getRole().name(), u.isMustChangePassword(),
                u.getEmployeeId(), u.getCustomerId());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(SecurityUtils.currentUser().getId(), req);
        return ResponseEntity.noContent().build();
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}

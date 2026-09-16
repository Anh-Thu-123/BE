package com.nagare.identity.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.nagare.identity.model.Role;
import com.nagare.identity.model.User;
import com.nagare.identity.model.UserStatus;
import com.nagare.identity.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Bay so 6 - khoa tai khoan phai co hieu luc NGAY qua tokenVersion, khong doi access token
 * het han sau 15 phut. Test nay mo phong: token cu (tokenVersion=0) van con "song" nhung
 * user trong DB da bi khoa/tang tokenVersion -> request phai KHONG duoc xac thuc.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock UserRepository userRepository;
    FilterChain chain = (req, res) -> {};

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private JwtService realJwtService() {
        return new JwtService("test-secret-key-must-be-long-enough-for-hmac-256-please");
    }

    @Test
    void khongXacThuc_khiTokenVersionLechDoTaiKhoanBiKhoa() throws Exception {
        JwtService jwtService = realJwtService();
        String userId = "u1";
        // Token phat hanh luc con ACTIVE, tokenVersion = 0
        String token = jwtService.generateAccessToken(userId, "hdv1", "TOUR_GUIDE", 0);

        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setUsername("hdv1");
        currentUser.setRole(Role.TOUR_GUIDE);
        currentUser.setStatus(UserStatus.LOCKED); // vua bi khoa
        currentUser.setTokenVersion(1); // da tang len sau khi khoa

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Token cu phai bi tu choi ngay ca khi chua het han 15 phut, vi tokenVersion da lech");
    }

    @Test
    void xacThucBinhThuong_khiTokenVersionKhop() throws Exception {
        JwtService jwtService = realJwtService();
        String userId = "u2";
        String token = jwtService.generateAccessToken(userId, "cskh1", "MKT_STAFF", 0);

        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setUsername("cskh1");
        currentUser.setRole(Role.MKT_STAFF);
        currentUser.setStatus(UserStatus.ACTIVE);
        currentUser.setTokenVersion(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

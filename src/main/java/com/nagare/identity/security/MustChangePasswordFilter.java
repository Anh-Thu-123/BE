package com.nagare.identity.security;

import com.nagare.common.error.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * mustChangePassword=true thi moi API tru /api/auth/change-password va /api/auth/logout,/me
 * deu tra 403 - buoc nhan su moi cap tai khoan phai doi mat khau tam truoc khi lam bat cu viec gi.
 */
@Component
public class MustChangePasswordFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] ALLOWED_WHEN_MUST_CHANGE = {
            "/api/auth/change-password",
            "/api/auth/logout",
            "/api/auth/me",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/register"
    };

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String path = request.getRequestURI();

        boolean allowed = false;
        for (String p : ALLOWED_WHEN_MUST_CHANGE) {
            if (path.equals(p)) { allowed = true; break; }
        }

        if (!allowed && auth != null && auth.getPrincipal() instanceof UserPrincipal principal
                && principal.isMustChangePassword()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    ApiError.of("MUST_CHANGE_PASSWORD", "Ban phai doi mat khau tam truoc khi tiep tuc")));
            return;
        }
        filterChain.doFilter(request, response);
    }
}

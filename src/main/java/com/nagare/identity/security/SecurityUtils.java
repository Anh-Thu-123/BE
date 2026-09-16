package com.nagare.identity.security;

import com.nagare.common.error.ApiException;
import org.springframework.security.core.context.SecurityContextHolder;

/** Tien ich lay nguoi dung hien tai - dung de dua dieu kien "cua minh" vao cau truy van Mongo. */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw ApiException.unauthorized("Chua dang nhap");
        }
        return principal;
    }

    public static UserPrincipal currentUserOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal;
    }
}

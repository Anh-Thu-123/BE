package com.nagare.identity.security;

import com.nagare.identity.model.Role;
import com.nagare.identity.model.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String passwordHash;
    private final Role role;
    private final boolean active;
    private final boolean mustChangePassword;
    private final long tokenVersion;
    private final String employeeId;
    private final String customerId;

    public UserPrincipal(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.passwordHash = u.getPasswordHash();
        this.role = u.getRole();
        this.active = u.getStatus() == com.nagare.identity.model.UserStatus.ACTIVE;
        this.mustChangePassword = u.isMustChangePassword();
        this.tokenVersion = u.getTokenVersion();
        this.employeeId = u.getEmployeeId();
        this.customerId = u.getCustomerId();
    }

    public String getId() { return id; }
    public Role getRole() { return role; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public long getTokenVersion() { return tokenVersion; }
    public String getEmployeeId() { return employeeId; }
    public String getCustomerId() { return customerId; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() { return passwordHash; }
    @Override
    public String getUsername() { return username; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return active; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return active; }
}

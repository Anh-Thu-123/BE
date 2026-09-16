package com.nagare.identity.model;

import java.time.Instant;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.nagare.common.model.AuditableEntity;

/**
 * users: mot ban ghi cho ca khach lan nhan su, phan biet bang role.
 * tokenVersion la co che khoa tai khoan co hieu luc NGAY (khong doi access token het han sau 15 phut) -
 * xem bay so 6 trong CLAUDE.md / muc 11.31 ban thiet ke.
 */
@Document(collection = "users")
public class User extends AuditableEntity {

    @Indexed(unique = true)
    private String username;

    private String passwordHash;

    private Role role;

    private UserStatus status = UserStatus.ACTIVE;

    /** Tang moi khi khoa/mo khoa hoac doi mat khau de vo hieu token cu ngay lap tuc. */
    private long tokenVersion = 0;

    private boolean mustChangePassword = false;

    private String employeeId;

    private String customerId;

    private Instant lastLoginAt;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public long getTokenVersion() { return tokenVersion; }
    public void setTokenVersion(long tokenVersion) { this.tokenVersion = tokenVersion; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}

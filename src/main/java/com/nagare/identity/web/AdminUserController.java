package com.nagare.identity.web;

import com.nagare.common.counter.SequenceGeneratorService;
import com.nagare.common.error.ApiException;
import com.nagare.hr.model.Employee;
import com.nagare.hr.repo.EmployeeRepository;
import com.nagare.identity.model.Role;
import com.nagare.identity.model.User;
import com.nagare.identity.model.UserStatus;
import com.nagare.identity.repo.UserRepository;
import com.nagare.identity.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/** Cap va khoa tai khoan: chi Giam doc va Thu ky, theo ma tran phan quyen muc 04. */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAnyRole('DIRECTOR','SECRETARY')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final SecureRandom random = new SecureRandom();

    public AdminUserController(UserRepository userRepository, EmployeeRepository employeeRepository,
                                PasswordEncoder passwordEncoder, AuthService authService,
                                SequenceGeneratorService sequenceGeneratorService) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    /**
     * fullName/department bat buoc cho vai tro noi bo - muc 04 ghi ro "Thu ky tao tai khoan
     * kem chuc danh va phong ban". employeeId chi dung khi cap tai khoan cho mot Employee
     * co san (vi du cap lai sau khi mat tai khoan); binh thuong De trong de tao Employee moi.
     */
    public record CreateUserRequest(@NotBlank String username, @NotBlank String role, String employeeId,
                                     String fullName, String position, String department, String phone) {}
    public record StatusRequest(@NotBlank String status) {}
    public record TempPasswordResponse(String username, String tempPassword) {}

    @GetMapping
    public List<User> list() {
        return userRepository.findAll();
    }

    /**
     * Sinh mat khau tam va bat mustChangePassword - khong co duong nao tu nang minh len vai tro noi bo.
     * Tao dong thoi ho so Employee neu chua truyen employeeId co san, vi mot tai khoan noi bo
     * khong co gi de hien thi/phan cong neu khong co ho so nhan su di kem (bug thuc te: truoc day
     * fullName/department bi Jackson am tham bo qua vi CreateUserRequest khong co truong nay,
     * nen GET /api/employees luon rong du da "cap tai khoan" cho ca chuc danh Van hanh).
     */
    @Transactional
    @PostMapping
    public TempPasswordResponse create(@org.springframework.web.bind.annotation.RequestBody CreateUserRequest req) {
        String username = req.username().toLowerCase().trim();
        if (userRepository.existsByUsername(username)) {
            throw ApiException.conflict("USERNAME_TAKEN", "Ten dang nhap da ton tai");
        }
        Role role;
        try {
            role = Role.valueOf(req.role());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("INVALID_ROLE", "Vai tro khong hop le");
        }
        if (role == Role.CUSTOMER) {
            throw ApiException.badRequest("INVALID_ROLE", "Khong cap tai khoan CUSTOMER qua duong nay");
        }

        String employeeId = req.employeeId();
        if ((employeeId == null || employeeId.isBlank()) && req.fullName() != null && !req.fullName().isBlank()) {
            Employee employee = new Employee();
            employee.setCode(sequenceGeneratorService.nextCode("NV"));
            employee.setFullName(req.fullName());
            employee.setPosition(req.position());
            employee.setPhone(req.phone());
            if (req.department() != null && !req.department().isBlank()) {
                try {
                    employee.setDepartment(Employee.Department.valueOf(req.department()));
                } catch (IllegalArgumentException e) {
                    throw ApiException.badRequest("INVALID_DEPARTMENT", "Phong ban khong hop le");
                }
            }
            employee.setStatus(Employee.Status.ACTIVE);
            employee = employeeRepository.save(employee);
            employeeId = employee.getId();
        }

        String tempPassword = generateTempPassword();
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(tempPassword));
        u.setRole(role);
        u.setStatus(UserStatus.ACTIVE);
        u.setMustChangePassword(true);
        u.setEmployeeId(employeeId);
        User saved = userRepository.save(u);

        if (employeeId != null) {
            employeeRepository.findById(employeeId).ifPresent(employee -> {
                employee.setUserId(saved.getId());
                employeeRepository.save(employee);
            });
        }
        return new TempPasswordResponse(username, tempPassword);
    }

    @PatchMapping("/{id}/status")
    public void setStatus(@PathVariable String id, @org.springframework.web.bind.annotation.RequestBody StatusRequest req) {
        UserStatus status;
        try {
            status = UserStatus.valueOf(req.status());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("INVALID_STATUS", "Trang thai khong hop le");
        }
        authService.setStatus(id, status);
    }

    @PostMapping("/{id}/reset-password")
    public TempPasswordResponse resetPassword(@PathVariable String id) {
        User u = userRepository.findById(id).orElseThrow(() -> ApiException.notFound("Tai khoan"));
        String tempPassword = generateTempPassword();
        u.setPasswordHash(passwordEncoder.encode(tempPassword));
        u.setMustChangePassword(true);
        u.setTokenVersion(u.getTokenVersion() + 1);
        userRepository.save(u);
        return new TempPasswordResponse(u.getUsername(), tempPassword);
    }

    private String generateTempPassword() {
        byte[] bytes = new byte[9];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

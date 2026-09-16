package com.nagare.identity;

import com.nagare.identity.model.Role;
import com.nagare.identity.model.User;
import com.nagare.identity.model.UserStatus;
import com.nagare.identity.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Tai khoan Giam doc dau tien: ai cap tai khoan cho Thu ky? Khi khoi dong lan dau tren
 * mot co so du lieu rong (khong con user nao), tu tao tai khoan Giam doc tu bien moi truong
 * BOOTSTRAP_ADMIN_USER / BOOTSTRAP_ADMIN_PASSWORD, bat san mustChangePassword.
 */
@Component
public class BootstrapAdminRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin-user:}")
    private String bootstrapUser;

    @Value("${app.bootstrap-admin-password:}")
    private String bootstrapPassword;

    public BootstrapAdminRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return; // da co du lieu, bo qua buoc bootstrap
        }
        if (bootstrapUser.isBlank() || bootstrapPassword.isBlank()) {
            log.warn("Chua cau hinh BOOTSTRAP_ADMIN_USER/BOOTSTRAP_ADMIN_PASSWORD - bo qua tao tai khoan Giam doc dau tien.");
            return;
        }
        User admin = new User();
        admin.setUsername(bootstrapUser.toLowerCase().trim());
        admin.setPasswordHash(passwordEncoder.encode(bootstrapPassword));
        admin.setRole(Role.DIRECTOR);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setMustChangePassword(true);
        userRepository.save(admin);
        log.info("Da tao tai khoan Giam doc khoi tao: {}", admin.getUsername());
    }
}

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
        // Kiem tra rieng vai tro DIRECTOR, khong phai tong so user: neu khach tu dang ky
        // truoc khi bien BOOTSTRAP_ADMIN_USER/PASSWORD duoc cau hinh, dieu kien "count() > 0"
        // se khoa vinh vien viec tao tai khoan Giam doc dau tien - day la loi thuc te gap phai
        // khi trien khai (mot khach dang ky thu tren production truoc khi Render duoc cap bien).
        if (userRepository.countByRole(Role.DIRECTOR) > 0) {
            return; // da co Giam doc, bo qua buoc bootstrap
        }
        if (bootstrapUser.isBlank() || bootstrapPassword.isBlank()) {
            log.warn("Chua cau hinh BOOTSTRAP_ADMIN_USER/BOOTSTRAP_ADMIN_PASSWORD - bo qua tao tai khoan Giam doc dau tien.");
            return;
        }
        String username = bootstrapUser.toLowerCase().trim();
        if (userRepository.existsByUsername(username)) {
            log.warn("Username '{}' da ton tai (vd. khach da dang ky trung ten) - khong the dung lam tai khoan "
                    + "Giam doc khoi tao. Doi BOOTSTRAP_ADMIN_USER sang mot username khac roi deploy lai.", username);
            return;
        }
        User admin = new User();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(bootstrapPassword));
        admin.setRole(Role.DIRECTOR);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setMustChangePassword(true);
        userRepository.save(admin);
        log.info("Da tao tai khoan Giam doc khoi tao: {}", admin.getUsername());
    }
}

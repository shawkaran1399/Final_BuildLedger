package com.buildledger.bootstrap;

import com.buildledger.entity.User;
import com.buildledger.enums.Role;
import com.buildledger.enums.UserStatus;
import com.buildledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createDefaultAdminIfNotExists();
    }

    private void createDefaultAdminIfNotExists() {
        if (userRepository.existsByUsername("admin")) {
            log.info("Default admin user already exists. Skipping bootstrap.");
            return;
        }

        log.info("Creating default admin user...");

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .name("System Administrator")
                .role(Role.ADMIN)
                .email("admin@buildledger.com")
                .phone("+1-000-000-0000")
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(admin);
        log.info("✅ Default admin user created. username=admin, password=admin123");
        log.info("⚠️  IMPORTANT: Change the default admin password immediately in production!");
    }
}

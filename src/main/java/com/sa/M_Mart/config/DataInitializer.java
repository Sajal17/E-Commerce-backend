package com.sa.M_Mart.config;

import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.Role;
import com.sa.M_Mart.model.UserRole;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superadmin.username}")
    private String superAdminUsername;

    @Value("${superadmin.password}")
    private String superAdminPassword;

    @Value("${superadmin.email}")
    private String superAdminEmail;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername(superAdminUsername)) {
            AppUser superAdmin = AppUser.builder()
                    .username(superAdminUsername)
                    .password(passwordEncoder.encode(superAdminPassword))
                    .email(superAdminEmail)
                    .verified(true)
                    .roles(new HashSet<>()) // empty set initially
                    .build();

            UserRole superAdminRole = new UserRole();
            superAdminRole.setRole(Role.ROLE_SUPERADMIN);
            superAdminRole.setUser(superAdmin);

            superAdmin.getRoles().add(superAdminRole);
            userRepository.save(superAdmin);
        } else {
            System.out.println("Superadmin already exists: " + superAdminUsername);
        }
    }
}

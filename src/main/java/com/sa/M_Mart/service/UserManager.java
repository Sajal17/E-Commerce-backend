package com.sa.M_Mart.service;

import com.sa.M_Mart.exception.ApiException;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.UserRole;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser createUser(String username, String password,
                               String email, String phone, Set<UserRole> roles) {

        if (username == null || username.isBlank()) {
            throw new ApiException("Username is required",400);
        }
        if (userRepository.existsByUsername(username)) {
            throw new ApiException("Username already exists",400);
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new ApiException("Email already exists",400);
        }
        if (phone != null && userRepository.existsByPhoneNumber(phone)) {
            throw new ApiException("Phone number already exists",400);
        }

        AppUser user = AppUser.builder()
                .username(username)
                .email(email)
                .phoneNumber(phone)
                .password(passwordEncoder.encode(password))
                .roles(roles)  // backend assigns roles
                .verified(false)  // or false if you want email verification
                .build();

        return userRepository.save(user);
    }

}

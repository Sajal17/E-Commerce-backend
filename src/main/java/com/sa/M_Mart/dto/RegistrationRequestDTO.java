package com.sa.M_Mart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public record RegistrationRequestDTO(
        @NotBlank(message = "Username is required")
        String username,  // can be email or phone

        @Email(message = "Invalid email format")
        String email,

        String phoneNumber,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 20, message = "Password must be at least 8 characters")
        String password,

        @NotBlank
        String confirmPassword,

        String firstName,
        String lastName,

        String role  // USER, ADMIN, SELLER
) {}

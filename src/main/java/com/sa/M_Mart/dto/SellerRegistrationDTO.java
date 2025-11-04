package com.sa.M_Mart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerRegistrationDTO(
        @NotBlank(message = "Seller username can't be blank")
        String username,
        String companyName,
        @Email(message = "Invalid email")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 20, message = "Password must be at least 8 characters")
        String password,
        @NotBlank
        String confirmPassword,
        String phoneNumber,
        String businessAddress,
        String bankAccountNumber,
        String ifscCode,
        String bankName,
        String branchName,
        boolean verified,
        boolean active
) {}

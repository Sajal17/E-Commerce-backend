package com.sa.M_Mart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateSellerProfileRequestDTO(
        @Email(message = "Invalid email")
        String email,
        String phoneNumber,
        @NotBlank
        String companyName,
        String businessAddress,
        String bankAccountNumber,
        String ifscCode,
        String bankName,
        String branchName
) {}

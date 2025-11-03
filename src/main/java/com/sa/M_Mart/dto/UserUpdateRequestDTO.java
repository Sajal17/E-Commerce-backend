package com.sa.M_Mart.dto;

import jakarta.validation.constraints.Email;

import java.util.List;

public record UserUpdateRequestDTO(
        @Email(message = "Invalid email format")
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        List<AddressDTO> addresses
) {}

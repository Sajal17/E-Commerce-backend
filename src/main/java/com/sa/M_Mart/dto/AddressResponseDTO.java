package com.sa.M_Mart.dto;

public record AddressResponseDTO(
        Long id,
        String fullName,
        String phone,
        String street,
        String city,
        String state,
        String country,
        String zip,
        Long userId
) { }

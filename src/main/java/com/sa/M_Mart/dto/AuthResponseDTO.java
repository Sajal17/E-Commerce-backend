package com.sa.M_Mart.dto;

import lombok.*;


public record AuthResponseDTO(
        String accessToken,     // JWT token
        String tokenType,       // Standard type (e.g., "Bearer")
        String username,        // For client display
        String role
) {
    // Optional: Default tokenType if not provided
    public AuthResponseDTO(String accessToken, String username, String role) {
        this(accessToken, "Bearer", username, role);
    }
}

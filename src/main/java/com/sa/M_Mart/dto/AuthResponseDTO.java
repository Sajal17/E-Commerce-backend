package com.sa.M_Mart.dto;



public record AuthResponseDTO(
        String accessToken,
        String tokenType,
        String username,
        String role
) {
    public AuthResponseDTO(String accessToken, String username, String role) {
        this(accessToken, "Bearer", username, role);
    }
}

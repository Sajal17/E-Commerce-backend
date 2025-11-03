package com.sa.M_Mart.dto;


import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(Instant.now(), 200, message, data);
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return new ApiResponse<>(Instant.now(), status, message, null);
    }
}

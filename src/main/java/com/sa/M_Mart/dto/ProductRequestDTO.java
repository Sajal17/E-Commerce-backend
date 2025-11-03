package com.sa.M_Mart.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @Size(max = 200, message = "Description max 200 chars")
        String description,

        @NotBlank(message = "Brand is required")
        String brand,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Category is required")
        String category,

        LocalDate releaseDate,

        @NotNull(message = "Available status is required")
        Boolean available,

        @Min(value = 0, message = "Quantity must be >= 0")
        int quantity,

        String imageUrl,

        String imageType
) {}

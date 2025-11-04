package com.sa.M_Mart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;


public record ProductResponseDTO(
        @JsonProperty("productId")
        Long id,
        String name,
        String description,
        String brand,
        BigDecimal price,
        String category,
        LocalDate releaseDate,
        boolean available,
        int quantity,
        Long sellerId,
        boolean verified,
        boolean isActive,
        String imageUrl,
        String imageType
) {}
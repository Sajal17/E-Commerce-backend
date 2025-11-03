package com.sa.M_Mart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

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
        String imageUrl,  // full URL
        String imageType
) {}

//private final Long sellerId;
//private final boolean verified;
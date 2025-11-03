package com.sa.M_Mart.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record OrderItemDTO(
        Long productId,
        String productName,
        String imageUrl,
        Integer quantity,
        BigDecimal price
) implements Serializable {}

package com.sa.M_Mart.dto;

import java.math.BigDecimal;


public record CartItemDTO (

    Long productId,
    String name,
    String brand,
    Integer quantity,
    BigDecimal price,
    String imageUrl
){}

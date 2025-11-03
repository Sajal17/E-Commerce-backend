package com.sa.M_Mart.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        Long customerId,
        List<OrderItemDTO> items,
        BigDecimal totalPrice,
        String shippingAddress,
        String paymentStatus,
        String orderStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {}


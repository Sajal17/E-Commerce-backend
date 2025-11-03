package com.sa.M_Mart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

public record PaymentDTO(
        Long id,
        Long orderId,
        Long customerId,
        BigDecimal amount,
        String method,
        String status,
        String transactionId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {}

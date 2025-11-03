package com.sa.M_Mart.dto;

import java.math.BigDecimal;
import java.io.Serializable;

public record CreatePaymentRequest(
        Long orderId,
        Long customerId,
        BigDecimal amount,
        String method
) implements Serializable {}


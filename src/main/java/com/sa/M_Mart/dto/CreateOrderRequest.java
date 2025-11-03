package com.sa.M_Mart.dto;

import java.io.Serializable;
import java.util.List;

public record CreateOrderRequest(
        Long customerId,
        List<OrderItemRequest> items,
        String shippingAddress,
        String paymentMethod
) implements Serializable {

    public record OrderItemRequest(
            Long productId,
            Integer quantity
    ) implements Serializable {}
}

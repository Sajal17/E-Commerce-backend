package com.sa.M_Mart.dto;

import java.util.List;

public record CartDTO (

    Long cartId,
    String username,
    List<CartItemDTO>items
){ }

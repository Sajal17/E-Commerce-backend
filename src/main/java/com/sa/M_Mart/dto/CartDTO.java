package com.sa.M_Mart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public record CartDTO (

    Long cartId,
    String username,
    List<CartItemDTO>items
){ }

package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.CartDTO;

public interface CartService {

    CartDTO getCart(String username);
    CartDTO addItemToCart(String username,Long productId,Integer quantity);
    CartDTO removeItemFromCart(String username, Long productId);
    CartDTO updateItemQuantity(String username, Long productId, Integer quantity);

    void clearCart(String username);

}

package com.sa.M_Mart.controller;

import com.sa.M_Mart.dto.CartDTO;
import com.sa.M_Mart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.getCart(username));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addItem(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.addItemToCart(username, productId, quantity));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartDTO> removeItem(
            Authentication authentication,
            @RequestParam Long productId) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.removeItemFromCart(username, productId));
    }
    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateQuantity(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.updateItemQuantity(username, productId, quantity));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Authentication authentication) {
        String username = authentication.getName();
        cartService.clearCart(username);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}


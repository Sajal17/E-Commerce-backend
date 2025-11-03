package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.CartDTO;
import com.sa.M_Mart.dto.CartItemDTO;
import com.sa.M_Mart.model.*;
import com.sa.M_Mart.repository.CartItemRepository;
import com.sa.M_Mart.repository.CartRepository;
import com.sa.M_Mart.repository.ProductRepository;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    @Override
    public CartDTO getCart(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        return toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(String username, Long productId, Integer quantity) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.addItem(newItem);
        }
        Cart updated = cartRepository.save(cart);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(String username, Long productId) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        Cart updated = cartRepository.save(cart);
        return toDTO(updated);
    }

    @Override
    public void clearCart(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    public CartDTO updateItemQuantity(String username, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().forEach(item -> {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
            }
        });

        cartRepository.save(cart);
        return toDTO(cart);
    }

    private CartDTO toDTO(Cart cart) {
        return new CartDTO(
                cart.getId(),
                cart.getUser().getUsername(),
                cart.getItems().stream().map(item ->
                                new CartItemDTO(
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getProduct().getBrand(),
                                        item.getQuantity(),
                                        item.getProduct().getPrice(),
                                        item.getProduct().getImageUrl()
                                ))
                        .collect(Collectors.toList())
        );
    }
}
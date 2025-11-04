package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.ProductRequestDTO;
import com.sa.M_Mart.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO addProduct(ProductRequestDTO request, Long sellerId,String imageUrl);

    ProductResponseDTO updateProduct(Long productId, ProductRequestDTO request, Long sellerId,String imageUrl);

    void deleteProduct(Long productId, Long sellerId);

    ProductResponseDTO getProduct(Long productId);

    List<ProductResponseDTO> getAllProducts();

    List<ProductResponseDTO> getProductsByCategory(String category);

    List<ProductResponseDTO> getProductsBySellerID(Long sellerId);

    List<ProductResponseDTO> searchProducts(String keyword);

    Long getSellerIdByUsername(Long sellerId);
}

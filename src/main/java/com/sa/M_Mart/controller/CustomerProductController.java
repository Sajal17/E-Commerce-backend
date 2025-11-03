package com.sa.M_Mart.controller;


import com.sa.M_Mart.dto.FeaturedProductDTO;
import com.sa.M_Mart.dto.ProductResponseDTO;
import com.sa.M_Mart.model.Product;
import com.sa.M_Mart.repository.ProductRepository;
import com.sa.M_Mart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    /** GET all products */
    @GetMapping
    public List<ProductResponseDTO> getProducts(@RequestParam(required = false) String category) {
        if (category != null) {
            return productService.getProductsByCategory(category);
        }
        return productService.getAllProducts();
    }
    /** GET product by ID */
    @GetMapping("/{id}")
    public ProductResponseDTO getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/search")
    public List<ProductResponseDTO> search(@RequestParam String query) {

        return productService.searchProducts(query);
    }
    @GetMapping("/featured")
    public List<FeaturedProductDTO> getFeaturedProducts(){
        return productRepository.findByFeaturedTrue()
                .stream()
                .map(p-> new FeaturedProductDTO(
                        p.getId(),
                        p.getName(),
                        p.getImageUrl()
                ))
                .toList();
    }

}

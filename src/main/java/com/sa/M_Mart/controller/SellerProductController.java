package com.sa.M_Mart.controller;

import com.sa.M_Mart.dto.OrderDTO;
import com.sa.M_Mart.dto.PageResponse;
import com.sa.M_Mart.dto.ProductRequestDTO;
import com.sa.M_Mart.dto.ProductResponseDTO;
import com.sa.M_Mart.model.SellerProfile;
import com.sa.M_Mart.repository.SellerRepository;
import com.sa.M_Mart.security.UserPrincipal;
import com.sa.M_Mart.service.ImageUploadService;
import com.sa.M_Mart.service.OrderService;
import com.sa.M_Mart.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerProductController {

    private final ProductService productService;
    private final OrderService orderService;
    private final SellerRepository sellerRepository;
    private final ImageUploadService imageUploadService;

    // Get all products by seller
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(@AuthenticationPrincipal UserPrincipal principal) {
        Long sellerId = principal.getUser().getId();
        List<ProductResponseDTO> products = productService.getProductsBySellerID(sellerId);
        return ResponseEntity.ok(products);
    }

    // Get single product by ID
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    // Add new product
    @PostMapping(value = "/products", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductResponseDTO> addProduct(
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam BigDecimal price,
            @RequestParam int quantity,
            @RequestParam String description,
            @RequestParam(required = false) String releaseDate,
            @RequestParam Boolean available,
            @RequestParam(required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {

        // Build DTO manually
        ProductRequestDTO dto = new ProductRequestDTO(
                name, description, brand, price, category,
                releaseDate != null ? LocalDate.parse(releaseDate) : null,
                available, quantity, null, null
        );

        // Upload image
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = imageUploadService.uploadImage(imageFile, "products", name, false);
        }

        // Save product
        ProductResponseDTO saved = productService.addProduct(dto, principal.getUser().getId(), imageUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }



    // edit product
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long productId,
            @RequestPart("product") @Valid ProductRequestDTO productRequestDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {

        // Get sellerId from authenticated user
        Long sellerId = principal.getUser().getId();
        // Upload image if present
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = imageUploadService.uploadImage(imageFile, "products", productRequestDTO.name(), false);
        }

        // Update product
        ProductResponseDTO updated = productService.updateProduct(productId, productRequestDTO, sellerId, imageUrl);
        return ResponseEntity.ok(updated);
    }

    // Delete product
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        Long sellerId = principal.getUser().getId();
        productService.deleteProduct(productId, sellerId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // Search products by keyword for a seller
    @GetMapping("/{sellerId}/products/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @PathVariable Long sellerId,     //  from URL
            @RequestParam String keyword     //  from query param
    ) {
        List<ProductResponseDTO> products = productService.searchProducts(keyword)
                .stream()
                .filter(p -> p.sellerId() != null && p.sellerId().equals(sellerId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
    @GetMapping("/orders")
    public ResponseEntity<?> getOrdersForSeller(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = principal.getUser().getId();
        SellerProfile sellerProfile = sellerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
        Long sellerId = sellerProfile.getId();

        Page<OrderDTO> orderPage = orderService.getOrdersBySellerId(sellerId, page, size);

        // Build stable JSON manually
        return ResponseEntity.ok(
                new PageResponse<>(
                        orderPage.getContent(),
                        orderPage.getNumber(),
                        orderPage.getSize(),
                        orderPage.getTotalElements(),
                        orderPage.getTotalPages(),
                        orderPage.isLast()
                )
        );
    }


    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

}

package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.ProductRequestDTO;
import com.sa.M_Mart.dto.ProductResponseDTO;
import com.sa.M_Mart.exception.ApiException;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.Product;
import com.sa.M_Mart.model.SellerProfile;
import com.sa.M_Mart.repository.ProductRepository;
import com.sa.M_Mart.repository.SellerRepository;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    @Override
    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO request, Long sellerId, String imageUrl) {

        AppUser seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        SellerProfile profile = seller.getSellerProfile();
        if (profile == null) {
            throw new ApiException("Seller profile not found for this user. Please complete seller registration.", 400);
        }

        if (!profile.isVerified()) {
            throw new ApiException("Your Profile is not verified. You cannot add products yet.", 403);
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .brand(request.brand())
                .price(request.price())
                .category(request.category())
                .releaseDate(request.releaseDate())
                .available(request.available())
                .imageUrl(imageUrl) // use uploaded image URL
                .imageType(request.imageType())
                .quantity(request.quantity())
                .seller(profile)
                .verified(true)
                .build();

        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO request, Long sellerId, String imageUrl) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        AppUser seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (!product.getSeller().getUser().getId().equals(seller.getId())) {
            throw new RuntimeException("Unauthorized to update this product");
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setReleaseDate(request.releaseDate());
        product.setAvailable(request.available());
        product.setQuantity(request.quantity());
        if (imageUrl != null && !imageUrl.isEmpty()) product.setImageUrl(imageUrl);
        product.setImageType(request.imageType());

        return toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        AppUser seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (!product.getSeller().getUser().getId().equals(seller.getId())) {
            throw new RuntimeException("Unauthorized to delete this product");
        }

        product.setActive(false);
        productRepository.save(product);
    }


    @Override
    public ProductResponseDTO getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findByVerifiedTrueAndIsActiveTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategoryIgnoreCaseAndIsActiveTrue(category);
        products.forEach(p -> System.out.println(p.getName() + " verified=" + p.isVerified()));
        return productRepository.findByCategoryIgnoreCaseAndIsActiveTrue(category)
                .stream()
                .filter(Product::isVerified)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> getProductsBySellerID(Long sellerId) {
        AppUser seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        SellerProfile profile = seller.getSellerProfile();
        if (profile == null) return List.of();

        return productRepository.findBySellerAndIsActiveTrue(profile)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



    @Override
    public List<ProductResponseDTO> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword)
                .stream()
                .filter(Product::isVerified)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getSellerIdByUsername(Long sellerId) {
        AppUser seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return seller.getId();
    }


    @Transactional
    public void restoreProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getUser().getId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to restore this product");
        }

        product.setActive(true);
        productRepository.save(product);
    }


    private ProductResponseDTO toDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getPrice(),
                product.getCategory(),
                product.getReleaseDate(),
                product.isAvailable(),
                product.getQuantity(),
                product.getSeller().getUser().getId(),
                product.isVerified(),
                product.getImageUrl(),
                product.getImageType()
        );
    }
}
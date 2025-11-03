package com.sa.M_Mart.repository;

import com.sa.M_Mart.model.Product;
import com.sa.M_Mart.model.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByCategoryIgnoreCase(String category);

    // Find all products for a seller
    List<Product> findBySeller(SellerProfile seller);

    // Optional: find active products for customers
    List<Product> findByVerifiedTrue();

    // Search by name containing keyword (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Extra custom queries only if needed
    Optional<Product> findByName(String name);

    List<Product>findByFeaturedTrue();
}
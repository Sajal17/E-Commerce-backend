package com.sa.M_Mart.repository;

import com.sa.M_Mart.model.Product;
import com.sa.M_Mart.model.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByCategoryIgnoreCaseAndIsActiveTrue(String category);

    List<Product> findBySellerAndIsActiveTrue(SellerProfile seller);

    List<Product> findByVerifiedTrueAndIsActiveTrue();

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String keyword);

    Optional<Product> findByName(String name);

    List<Product> findByIsActiveTrue();

    List<Product>findByFeaturedTrue();
}
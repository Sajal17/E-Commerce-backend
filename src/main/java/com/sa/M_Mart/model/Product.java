package com.sa.M_Mart.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products",
         indexes = {
              @Index(name = "idx_product_name",columnList = "name"),
              @Index(name = "idx_product_category",columnList = "category")
         })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "product_id",updatable = false,nullable = false)
      private Long id;

      @Column(nullable = false,length = 50)
      private String name;

      @Column(length = 200)
      private String description;

      @Column(length = 20)
      private String brand;

      @Column(precision = 10,scale = 2,nullable = false)
      private BigDecimal price;

      @Column(length = 20,nullable = false)
      private String category;

      private LocalDate releaseDate;

      @Column(nullable = false)
      private boolean available;

      @Column(nullable = false)
      private int quantity;

      @Column(name = "image_url", length = 500)
      private String imageUrl;

      @Column(name = "image_type",length = 25)
      private String imageType;

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "seller_id", nullable = false)
      private SellerProfile seller;

      @Column(name = "is_active")
      private boolean isActive = true;

      private boolean verified=false;

      private boolean featured;
}

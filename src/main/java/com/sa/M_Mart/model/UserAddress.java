package com.sa.M_Mart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max=20,message = "Street name too long")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Street is required")
    @Size(max=50,message = "Street name too long")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 20,message = "City name too long")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 20,message = "State name too long")
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 20,message = "Country name too long")
    @Column(nullable = false)
    private String country;

    @NotBlank(message = "ZIP is required")
    @Size(max = 20,message = "ZIP name too long")
    @Column(nullable = false)
    private String zip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private AppUser user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}

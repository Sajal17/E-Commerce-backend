package com.sa.M_Mart.repository;

import com.sa.M_Mart.model.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<SellerProfile,Long> {

    Optional<SellerProfile> findByUser_Username(String username);
    Optional<SellerProfile> findByUser_Id(Long userId);

}

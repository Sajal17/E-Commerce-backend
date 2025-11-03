package com.sa.M_Mart.repository;

import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUser(AppUser user);

    Optional<Cart>findByUserUsername(String username);
}

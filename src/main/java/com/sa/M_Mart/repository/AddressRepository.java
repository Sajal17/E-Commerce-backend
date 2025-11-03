package com.sa.M_Mart.repository;

import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<UserAddress,Long> {

    List<UserAddress> findByUser(AppUser user);
}

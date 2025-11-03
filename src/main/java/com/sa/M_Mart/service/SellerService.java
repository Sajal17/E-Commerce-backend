package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.SellerProfileDTO;
import com.sa.M_Mart.dto.SellerRegistrationDTO;
import com.sa.M_Mart.model.AppUser;

public interface SellerService {

    SellerProfileDTO registerSeller(SellerRegistrationDTO user);

    SellerProfileDTO getSellerProfile(String username);

    SellerProfileDTO updateSellerProfile(String username, SellerProfileDTO request, String jwtUsername);

    void verifySeller(String username, String adminUsername); // admin only
    void deleteSellerAccount(String sellerUsername, String jwtUsername);

}

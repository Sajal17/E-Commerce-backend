package com.sa.M_Mart.controller;

import com.sa.M_Mart.dto.SellerProfileDTO;
import com.sa.M_Mart.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final SellerService sellerService;

    // Get own profile
    @GetMapping("/profile")
    public ResponseEntity<SellerProfileDTO> getOwnProfile(Principal principal) {
        String username = principal.getName();
        SellerProfileDTO profile = sellerService.getSellerProfile(username);
        return ResponseEntity.ok(profile);
    }

    // Update own profile
    @PutMapping("/profile")
    public ResponseEntity<SellerProfileDTO> updateOwnProfile(@RequestBody SellerProfileDTO request,
                                                             Principal principal) {
        String username = principal.getName();
        SellerProfileDTO updatedProfile = sellerService.updateSellerProfile(username, request, username);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteOwnAccount(Principal principal) {
        String username = principal.getName();
        sellerService.deleteSellerAccount(username, username);
        return ResponseEntity.ok("Seller account deleted successfully");
    }

}


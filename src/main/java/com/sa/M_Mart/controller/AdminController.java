package com.sa.M_Mart.controller;

import com.sa.M_Mart.dto.*;
import com.sa.M_Mart.security.UserPrincipal;
import com.sa.M_Mart.service.AdminService;
import com.sa.M_Mart.service.ProductService;
import com.sa.M_Mart.service.SellerService;
import com.sa.M_Mart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final SellerService sellerService;
    private final UserService userService;
    private final ProductService productService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerAdmin(@RequestBody RegistrationRequestDTO request,
                                                        Principal principal) {
        String currentUsername = principal.getName();
        UserResponseDTO dto = adminService.createAdmin(request, currentUsername);
        return ResponseEntity.ok(ApiResponse.success("Admin registered successfully", dto));
    }


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getAdminProfile(Principal principal) {
        String username = principal.getName();
        UserResponseDTO dto = adminService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Admin profile retrieved", dto));
    }

    // Admin verifies seller
    @PostMapping("/seller/verify")
    public ResponseEntity<String> verifySeller(@RequestParam String username,
                                               Principal principal) {
        String adminUsername = principal.getName();
        sellerService.verifySeller(username, adminUsername);
        return ResponseEntity.ok("Seller verified successfully");
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<SellerProfileDTO> getSellerProfileAdmin(
            @PathVariable String username,
            Principal principal) {

        UserResponseDTO admin = userService.getUserByUsername(principal.getName());
        if (!admin.role().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SellerProfileDTO profile = sellerService.getSellerProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/users/{username}/role")
    public ResponseEntity<ApiResponse<?>> updateUserRole(@PathVariable String username,
                                                         @RequestBody UpdatedUserRoleRequestDTO request,
                                                         Principal principal) {
        String adminUsername = principal.getName();
        if (request.role() == null || request.role().isBlank()) {
            throw new RuntimeException("Role must not be empty");
        }

        Set<String> roles = Collections.singleton(request.role());
        UserResponseDTO dto = adminService.updateUserRole(username, roles, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", dto));
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable String username,
                                                     Principal principal) {
        String adminUsername = principal.getName();
        adminService.deleteUser(username, adminUsername);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

}

package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.SellerProfileDTO;
import com.sa.M_Mart.dto.SellerRegistrationDTO;
import com.sa.M_Mart.exception.ApiException;
import com.sa.M_Mart.model.*;
import com.sa.M_Mart.repository.ProductRepository;
import com.sa.M_Mart.repository.RefreshTokenRepository;
import com.sa.M_Mart.repository.SellerRepository;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final UserManager userManager;
    private final ProductRepository productRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    // Register seller
    @Override
    @Transactional
    public SellerProfileDTO registerSeller(SellerRegistrationDTO request) {

        //  Validate passwords
        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException("Passwords do not match", 400);
        }
        // Clean optional fields
        String email = request.email() != null && !request.email().isBlank() ? request.email().trim() : null;
        String phone = request.phoneNumber() != null && !request.phoneNumber().isBlank() ? request.phoneNumber().trim() : null;

        //  Check uniqueness only if present
        if (userRepository.existsByUsername(request.username()))
            throw new ApiException("Username already exists", 400);
        if (email != null && userRepository.existsByEmail(email))
            throw new ApiException("Email already exists", 400);
        if (phone != null && userRepository.existsByPhoneNumber(phone))
            throw new ApiException("Phone number already exists", 400);
        // Create user
        AppUser sellerUser= userManager.createUser(
                request.username(),
                request.password(),
                email,
                phone,
                new HashSet<>() // fixed role for users
        );

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ROLE_SELLER);
        userRole.setUser(sellerUser);

        sellerUser.getRoles().add(userRole);
        // Save user first
        sellerUser = userRepository.save(sellerUser);

        //  Create SellerProfile
        SellerProfile profile = SellerProfile.builder()
                .user(sellerUser)
                .companyName(request.companyName() != null ? request.companyName() : "N/A")
                .businessAddress(request.businessAddress() != null ? request.businessAddress() : "")
                .bankAccountNumber(request.bankAccountNumber() != null ? request.bankAccountNumber() : "")
                .ifscCode(request.ifscCode() != null ? request.ifscCode() : "")
                .bankName(request.bankName() != null ? request.bankName() : "")
                .branchName(request.branchName() != null ? request.branchName() : "")
                .active(true)
                .verified(false)
                .build();

        // Link back to user
        sellerUser.setSellerProfile(profile);

        // Save profile
        sellerRepository.save(profile);

        // Save user again to persist the link
        userRepository.save(sellerUser);

        // Return DTO
        return toDTO(profile);
    }

    @Override
    public SellerProfileDTO getSellerProfile(String sellerUsername) {
        SellerProfile seller = sellerRepository.findByUser_Username(sellerUsername)
                .orElseThrow(() -> new ApiException(
                        "Seller profile not found for this user. Please complete seller registration.", 404));

        return toDTO(seller);
    }

    @Override
    @Transactional
    public SellerProfileDTO updateSellerProfile(String sellerUsername, SellerProfileDTO request, String jwtUsername) {
        if (!sellerUsername.equals(jwtUsername)) {
            throw new RuntimeException("Unauthorized to update this profile");
        }

        SellerProfile seller = sellerRepository.findByUser_Username(sellerUsername)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        AppUser user = seller.getUser();

        // Update user fields
        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email());
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) user.setPhoneNumber(request.phoneNumber());
        if (request.firstName() != null && !request.firstName().isBlank()) user.setFirstName(request.firstName());
        if (request.lastName() != null && !request.lastName().isBlank()) user.setLastName(request.lastName());

        // Update seller fields
        if (request.companyName() != null && !request.companyName().isBlank()) seller.setCompanyName(request.companyName());
        if (request.businessAddress() != null && !request.businessAddress().isBlank()) seller.setBusinessAddress(request.businessAddress());
        if (request.bankAccountNumber() != null && !request.bankAccountNumber().isBlank()) seller.setBankAccountNumber(request.bankAccountNumber());
        if (request.ifscCode() != null && !request.ifscCode().isBlank()) seller.setIfscCode(request.ifscCode());
        if (request.bankName() != null && !request.bankName().isBlank()) seller.setBankName(request.bankName());
        if (request.branchName() != null && !request.branchName().isBlank()) seller.setBranchName(request.branchName());

        userRepository.save(user);
        sellerRepository.save(seller);

        return toDTO(seller);
    }

    @Override
    @Transactional
    public void verifySeller(String sellUsername, String adminUsername) {

        // 1. Validate admin
        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ApiException("Admin not found",404));
        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> r.getRole() == Role.ROLE_ADMIN || r.getRole() == Role.ROLE_SUPERADMIN);
        if (!isAdmin) {
            throw new ApiException("Only admins or superadmins can verify sellers", 403);
        }
        // 2. Fetch seller profile
        SellerProfile seller = sellerRepository.findByUser_Username(sellUsername)
                .orElseThrow(() -> new ApiException("Seller not found",404));

        // 3. Set verified and record who verified
        seller.setVerified(true);
        seller.setVerifiedBy(adminUsername); // Add this field in SellerProfile entity
        sellerRepository.save(seller);

    }

    @Override
    @Transactional
    public void deleteSellerAccount(String sellerUsername, String jwtUsername) {
        // Only allow the seller themselves to delete their account
        if (!sellerUsername.equals(jwtUsername)) {
            throw new ApiException("Unauthorized to delete this account", 403);
        }

        // Fetch the user and profile
        AppUser user = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new ApiException("User not found", 404));
        // Delete all refresh tokens
        refreshTokenRepository.deleteByUser(user);
        SellerProfile profile = user.getSellerProfile();
        if (profile != null) {
            // Delete all products by this seller
            List<Product> products = productRepository.findBySeller(profile);
            if (!products.isEmpty()) {
                productRepository.deleteAll(products);
            }

            // Remove the link to avoid Hibernate detached issues
            user.setSellerProfile(null);
            sellerRepository.delete(profile);
        }

        // Remove all roles for the user
        user.getRoles().clear();
        userRepository.save(user);

        // Delete user
        userRepository.delete(user);
    }


    private SellerProfileDTO toDTO(SellerProfile seller) {
        Set<Role> roles = seller.getUser().getRoles()
                .stream()
                .map(UserRole::getRole) // keep as Role enum
                .collect(Collectors.toSet());
        return new SellerProfileDTO(
                seller.getId(),
                seller.getUser().getUsername(),
                seller.getUser().getEmail(),
                seller.getUser().getPhoneNumber(),
                seller.getUser().getFirstName(),
                seller.getUser().getLastName(),
                seller.getCompanyName(),
                seller.getBusinessAddress(),
                seller.getBankAccountNumber(),
                seller.getIfscCode(),
                seller.getBankName(),
                seller.getBranchName(),
                seller.isVerified(),
                roles,
                seller.isActive()
                );
    }
}
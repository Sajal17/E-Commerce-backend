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

    @Override
    @Transactional
    public SellerProfileDTO registerSeller(SellerRegistrationDTO request) {

        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException("Passwords do not match", 400);
        }
        String email = request.email() != null && !request.email().isBlank() ? request.email().trim() : null;
        String phone = request.phoneNumber() != null && !request.phoneNumber().isBlank() ? request.phoneNumber().trim() : null;

        if (userRepository.existsByUsername(request.username()))
            throw new ApiException("Username already exists", 400);
        if (email != null && userRepository.existsByEmail(email))
            throw new ApiException("Email already exists", 400);
        if (phone != null && userRepository.existsByPhoneNumber(phone))
            throw new ApiException("Phone number already exists", 400);
        AppUser sellerUser= userManager.createUser(
                request.username(),
                request.password(),
                email,
                phone,
                new HashSet<>()
        );

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ROLE_SELLER);
        userRole.setUser(sellerUser);

        sellerUser.getRoles().add(userRole);
        sellerUser = userRepository.save(sellerUser);

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

        sellerUser.setSellerProfile(profile);

        sellerRepository.save(profile);

        userRepository.save(sellerUser);

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

        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email());
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) user.setPhoneNumber(request.phoneNumber());
        if (request.firstName() != null && !request.firstName().isBlank()) user.setFirstName(request.firstName());
        if (request.lastName() != null && !request.lastName().isBlank()) user.setLastName(request.lastName());

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

        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ApiException("Admin not found",404));
        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(r -> r.getRole() == Role.ROLE_ADMIN || r.getRole() == Role.ROLE_SUPERADMIN);
        if (!isAdmin) {
            throw new ApiException("Only admins or superadmins can verify sellers", 403);
        }
        SellerProfile seller = sellerRepository.findByUser_Username(sellUsername)
                .orElseThrow(() -> new ApiException("Seller not found",404));

        seller.setVerified(true);
        seller.setVerifiedBy(adminUsername);
        sellerRepository.save(seller);

    }

    @Override
    @Transactional
    public void deleteSellerAccount(String sellerUsername, String jwtUsername) {
        if (!sellerUsername.equals(jwtUsername)) {
            throw new ApiException("Unauthorized to delete this account", 403);
        }

        AppUser user = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new ApiException("User not found", 404));
        refreshTokenRepository.deleteByUser(user);
        SellerProfile profile = user.getSellerProfile();
        if (profile != null) {
            List<Product> products = productRepository.findBySellerAndIsActiveTrue(profile);
            if (!products.isEmpty()) {
                productRepository.deleteAll(products);
            }

            user.setSellerProfile(null);
            sellerRepository.delete(profile);
        }

        user.getRoles().clear();
        userRepository.save(user);

        userRepository.delete(user);
    }


    private SellerProfileDTO toDTO(SellerProfile seller) {
        Set<Role> roles = seller.getUser().getRoles()
                .stream()
                .map(UserRole::getRole)
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
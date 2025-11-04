package com.sa.M_Mart.controller;

import com.sa.M_Mart.dto.*;
import com.sa.M_Mart.service.AuthService;
import com.sa.M_Mart.service.SellerService;
import com.sa.M_Mart.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SellerService sellerService;
    private final UserService userService;

    @Transactional
    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody @Valid RegistrationRequestDTO request) {
        System.out.println("Register called for: " + request.username());

        UserResponseDTO dto = userService.registerUser(request);
          return ResponseEntity.ok(ApiResponse.success(
                  "User register successfully",dto));

    }

    @Transactional
    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponse<?>> registerSeller(@RequestBody @Valid SellerRegistrationDTO request) {
        System.out.println("seller");
        SellerProfileDTO dto = sellerService.registerSeller(request);

        return ResponseEntity.ok(ApiResponse.success("Seller registered successfully", dto));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequestDTO authRequest,
                                   HttpServletResponse response) {
        return authService.login(authRequest, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken",
                                      required = false) String refreshToken) {
        return authService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                         HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

}

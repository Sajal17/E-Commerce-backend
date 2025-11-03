package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.AuthRequestDTO;

import com.sa.M_Mart.model.RefreshToken;
import com.sa.M_Mart.model.UserRole;
import com.sa.M_Mart.repository.UserRepository;
import com.sa.M_Mart.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

   // private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> login(AuthRequestDTO authRequest, HttpServletResponse response) {
        String username = authRequest.username();
        String password = authRequest.password();

        return userRepository.findByPhoneNumber(username)
                .or(()->userRepository.findByUsername(username))
                .or(()->userRepository.findByEmail(username))
                .map(user -> {

                    //  Check password
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return ResponseEntity.status(401)
                                .body(Map.of("message", "Invalid credentials"));
                    }

                    //  Map Set<UserRole> → Set<Role> for JWT
                    Set<String> roles = (user.getRoles() != null ? user.getRoles() : Set.<UserRole>of())
                            .stream()
                            .map(r -> r.getRole())  // lambda, r is now UserRole
                            .map(Enum::name)
                            .collect(Collectors.toSet());
                    // Generate JWT + Refresh token
                    String accessToken = jwtService.generateAccessToken(user.getUsername(), roles);
                    // Create refresh token
                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                            .httpOnly(true)
                            .secure(true) // set true in production
                            .path("/api/auth/refresh")
                            .maxAge(30 * 24 * 60 * 60) // 30 days
                            .sameSite("Lax")
                            .build();

                    response.addHeader("Set-Cookie", cookie.toString());

                    return ResponseEntity.ok(Map.of("accessToken", accessToken));
                })
                .orElseGet(() -> ResponseEntity
                        .status(401)
                        .body(Map.of("message", "Invalid credentials or not verified")));
    }

    @Override
    public ResponseEntity<?> refreshAccessToken(String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token missing");
        }

        return refreshTokenService.findByToken(refreshToken)
                .map(rt -> {
                    //  Check if refresh token expired
                    if (refreshTokenService.isExpired(rt)) {
                        refreshTokenService.deleteByUser(rt.getUser());
                        return ResponseEntity.status(401).body("Refresh token expired");
                    }
                    //  Map roles for JWT
                    Set<String> roles = rt.getUser().getRoles()
                            .stream()
                            .map(UserRole::getRole)      // get Role enum from UserRole
                            .map(Enum::name)             // convert Role enum → String
                            .collect(Collectors.toSet());
                    // Generate new access token
                    String newAccessToken = jwtService.generateAccessToken(rt.getUser().getUsername(), roles);
                    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token")));
    }

    @Override
    public void logout(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            refreshTokenService.findByToken(refreshToken)
                    .ifPresent(rt -> refreshTokenService.deleteByUser(rt.getUser()));
        }
        // Clear cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // must be true in production
                .path("/api/auth/refresh")
                .maxAge(0) // delete immediately
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
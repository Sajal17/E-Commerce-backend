package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.AuthRequestDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> login(AuthRequestDTO authRequest, HttpServletResponse response);
    ResponseEntity<?> refreshAccessToken(String refreshToken);
    void logout(String refreshToken, HttpServletResponse response);

}

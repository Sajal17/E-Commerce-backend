package com.sa.M_Mart.controller;

import com.sa.M_Mart.dto.AddressDTO;
import com.sa.M_Mart.dto.UserResponseDTO;
import com.sa.M_Mart.dto.UserUpdateRequestDTO;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.UserAddress;
import com.sa.M_Mart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final UserService userService;

    // GET current user profile
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Principal principal) {
        UserResponseDTO dto = userService.getUserByUsername(principal.getName());
        System.out.println("🔹 JWT roles: " + principal.getName());
        return ResponseEntity.ok(dto); // here you need 'toDTO'
    }

    // UPDATE current user profile
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            Principal principal,
            @RequestBody UserUpdateRequestDTO dto
    ) {
        // Service already returns UserResponseDTO after updating
        UserResponseDTO updatedUserDTO = userService.updateUserProfile(principal.getName(), dto);

        return ResponseEntity.ok(updatedUserDTO);
    }

}
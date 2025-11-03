package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.RegistrationRequestDTO;
import com.sa.M_Mart.dto.UserResponseDTO;
import com.sa.M_Mart.dto.UserUpdateRequestDTO;
import com.sa.M_Mart.model.AppUser;

public interface UserService {

    UserResponseDTO registerUser(RegistrationRequestDTO user);

    // Fetch user profile by username
    UserResponseDTO getUserByUsername(String username);

    // Update user profile
    UserResponseDTO updateUserProfile(String username, UserUpdateRequestDTO updateRequest);
}


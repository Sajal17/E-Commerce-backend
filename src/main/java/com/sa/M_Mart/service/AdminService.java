package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.RegistrationRequestDTO;
import com.sa.M_Mart.dto.UserResponseDTO;

import java.util.Set;

public interface AdminService {

    UserResponseDTO createAdmin(RegistrationRequestDTO request, String createdByAdminUsername);
    UserResponseDTO getUserByUsername(String username);
    UserResponseDTO updateUserRole(String username, Set<String> roles, String updatedByAdminUsername);

    void deleteUser(String username, String adminUsername);

}

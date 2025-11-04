package com.sa.M_Mart.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatedUserRoleRequestDTO(

    @NotBlank(message = "Role can't be Blank")
    String role
){ }

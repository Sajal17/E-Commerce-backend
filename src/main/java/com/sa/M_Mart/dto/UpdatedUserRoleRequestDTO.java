package com.sa.M_Mart.dto;

import com.sa.M_Mart.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public record UpdatedUserRoleRequestDTO(

    @NotBlank(message = "Role can't be Blank")
    String role
){ }

package com.sa.M_Mart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sa.M_Mart.model.Role;
import jakarta.validation.constraints.Email;

import java.util.Set;


public record SellerProfileDTO (

        @JsonProperty("id") Long id,
        @JsonProperty("username") String username,
        @Email(message = "Invalid formate")
        @JsonProperty("email") String email,
        @JsonProperty("phoneNumber") String phoneNumber,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,

        @JsonProperty("companyName") String companyName,
        @JsonProperty("businessAddress") String businessAddress,
        @JsonProperty("bankAccountNumber") String bankAccountNumber,
        @JsonProperty("ifscCode") String ifscCode,
        @JsonProperty("bankName") String bankName,
        @JsonProperty("branchName") String branchName,
        @JsonProperty("verified") boolean verified,
        @JsonProperty("role") Set<Role> role,
        @JsonProperty("active") boolean active
){ }

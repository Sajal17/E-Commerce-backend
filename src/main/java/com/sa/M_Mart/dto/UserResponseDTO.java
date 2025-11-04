package com.sa.M_Mart.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sa.M_Mart.model.Role;

import java.util.List;
import java.util.Set;

public record UserResponseDTO (

        @JsonProperty("id") Long id,
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("phoneNumber") String phoneNumber,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("addresses") List<AddressDTO> addresses,
        @JsonProperty("role") Set<Role> role,
        @JsonProperty("verified") boolean verified
){ }

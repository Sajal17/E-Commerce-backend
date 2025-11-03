package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.AddressDTO;
import com.sa.M_Mart.dto.AddressResponseDTO;

import java.util.List;

public interface AddressService {
    List<AddressResponseDTO> getAllAddresses(String username);
    AddressResponseDTO addAddress(String username, AddressDTO dto);
    AddressResponseDTO updateAddress(String username, Long id, AddressDTO dto);
    void deleteAddress(String username, Long id);
}

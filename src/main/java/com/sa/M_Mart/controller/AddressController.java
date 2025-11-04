package com.sa.M_Mart.controller;

import com.sa.M_Mart.dto.AddressDTO;
import com.sa.M_Mart.dto.AddressResponseDTO;
import com.sa.M_Mart.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> addAddress(
            @RequestBody AddressDTO dto,
            Principal principal
    ) {
        AddressResponseDTO saved = addressService.addAddress(principal.getName(), dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(Principal principal) {
        List<AddressResponseDTO> addresses = addressService.getAllAddresses(principal.getName());
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            Principal principal,
            @PathVariable Long id,
            @RequestBody AddressDTO dto
    ) {
        AddressResponseDTO updated = addressService.updateAddress(principal.getName(), id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(Principal principal, @PathVariable Long id) {
        addressService.deleteAddress(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}

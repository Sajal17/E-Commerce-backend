package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.AddressDTO;
import com.sa.M_Mart.dto.AddressResponseDTO;
import com.sa.M_Mart.exception.ApiException;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.UserAddress;
import com.sa.M_Mart.repository.AddressRepository;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponseDTO> getAllAddresses(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", 404));

        return addressRepository.findByUser(user).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponseDTO addAddress(String username, AddressDTO dto) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", 404));

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setFullName(dto.fullName());
        address.setPhone(dto.phone());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setCountry(dto.country());
        address.setZip(dto.zip());

        UserAddress saved = addressRepository.save(address);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public AddressResponseDTO updateAddress(String username, Long id, AddressDTO dto) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", 404));

        UserAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ApiException("Address not found", 404));

        if (!address.getUser().equals(user)) {
            throw new ApiException("Unauthorized access to this address", 403);
        }

        address.setFullName(dto.fullName());
        address.setPhone(dto.phone());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setCountry(dto.country());
        address.setZip(dto.zip());

        UserAddress updated = addressRepository.save(address);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteAddress(String username, Long id) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", 404));

        UserAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ApiException("Address not found", 404));

        if (!address.getUser().equals(user)) {
            throw new ApiException("Unauthorized", 403);
        }

        addressRepository.delete(address);
    }

    private AddressResponseDTO mapToResponseDTO(UserAddress a) {
        return new AddressResponseDTO(
                a.getId(),
                a.getFullName(),
                a.getPhone(),
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getCountry(),
                a.getZip(),
                a.getUser().getId()
        );
    }
}

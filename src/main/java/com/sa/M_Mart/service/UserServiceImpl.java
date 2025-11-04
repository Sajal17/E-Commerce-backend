package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.AddressDTO;
import com.sa.M_Mart.dto.RegistrationRequestDTO;
import com.sa.M_Mart.dto.UserResponseDTO;
import com.sa.M_Mart.dto.UserUpdateRequestDTO;
import com.sa.M_Mart.exception.ApiException;
import com.sa.M_Mart.model.*;
import com.sa.M_Mart.repository.CartRepository;
import com.sa.M_Mart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserManager userManager;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public UserResponseDTO registerUser(RegistrationRequestDTO request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException("Passwords do not match", 400);
        }

        AppUser user= userManager.createUser(
                request.username(),
                request.password(),
                request.email(),
                request.phoneNumber(),
                new HashSet<>()
        );

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ROLE_USER);
        userRole.setUser(user);

        user.getRoles().add(userRole);

        userRepository.save(user);

        boolean isBuyer = user.getRoles().stream()
                .anyMatch(r -> r.getRole() == Role.ROLE_USER);
        if (isBuyer) {
            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        } else {
            System.out.println("No cart created for non-buyer user.");
        }
        return mapToDTO(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserProfile(String username, UserUpdateRequestDTO request) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email());
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank())
            user.setPhoneNumber(request.phoneNumber());

        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.addresses() != null) {
            user.getAddresses().clear();

            for (AddressDTO addressDTO : request.addresses()) {
                UserAddress address = new UserAddress();
                address.setUser(user);
                address.setStreet(addressDTO.street());
                address.setCity(addressDTO.city());
                address.setState(addressDTO.state());
                address.setCountry(addressDTO.country());
                address.setZip(addressDTO.zip());
                user.getAddresses().add(address);
            }
        }

        userRepository.save(user);

        return mapToDTO(user);
    }

    private UserResponseDTO mapToDTO(AppUser user) {

        List<AddressDTO> addressDTOS = (user.getAddresses() != null ? user.getAddresses() : List.of())
                .stream()
                .map(addr -> {
                    UserAddress a = (UserAddress) addr;
                    return new AddressDTO(
                            a.getFullName()!=null ? a.getFullName() : "",
                            a.getPhone() !=null ? a.getPhone() :"",
                            a.getStreet() != null ? a.getStreet() : "",
                            a.getCity() != null ? a.getCity() : "",
                            a.getState() != null ? a.getState() : "",
                            a.getCountry() != null ? a.getCountry() : "",
                            a.getZip() != null ? a.getZip() : ""
                    );
                })
                .toList();

        Set<Role> roles = user.getRoles()
                .stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                addressDTOS,
                roles,
                user.isVerified()
        );
    }
}
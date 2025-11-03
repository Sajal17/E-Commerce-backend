package com.sa.M_Mart.service;

import com.sa.M_Mart.dto.RegistrationRequestDTO;
import com.sa.M_Mart.dto.UserResponseDTO;
import com.sa.M_Mart.model.AppUser;
import com.sa.M_Mart.model.Role;
import com.sa.M_Mart.model.UserRole;
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
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserManager userManager;

    // --- Interface methods ---
    @Override
    public UserResponseDTO getUserByUsername(String username) {
        AppUser user = findUser(username);
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO createAdmin(RegistrationRequestDTO request, String superAdminUsername) {
        AppUser superAdmin = findUser(superAdminUsername);
        boolean isSuperAdmin = superAdmin.getRoles().stream()
                .anyMatch(r -> r.getRole() == Role.ROLE_SUPERADMIN);
        if (!isSuperAdmin) {
            throw new RuntimeException("Only super admins can create admins");
        }

        if (userRepository.existsByUsername(request.username()) ||
                userRepository.existsByEmail(request.email()) ||
                (request.phoneNumber() != null && userRepository.existsByPhoneNumber(request.phoneNumber()))) {
            throw new RuntimeException("Username/email/phone already in use");
        }

        AppUser user = userManager.createUser(
                request.username(),
                request.password(),
                request.email(),
                request.phoneNumber(),
                new HashSet<>()
        );

// 2. Create role and attach back-reference
        UserRole userRole = new UserRole();
        userRole.setRole(Role.ROLE_ADMIN);
        userRole.setUser(user);

// 3. Add role to user
        user.getRoles().add(userRole);

// 4. Save user again
        userRepository.save(user);
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserRole(String username, Set<String> newRoles, String adminUsername) {
        AppUser admin = findUser(adminUsername);
        boolean isAdminOrSuper = admin.getRoles().stream()
                .anyMatch(r -> r.getRole() == Role.ROLE_ADMIN || r.getRole() == Role.ROLE_SUPERADMIN);
        if (!isAdminOrSuper) {
            throw new RuntimeException("Only admins or superadmins can perform this action");
        }

        AppUser user = findUser(username);
        Set<UserRole> updatedRoles = newRoles.stream()
                .map(roleStr -> {
                    UserRole ur = new UserRole();
                    ur.setRole(Role.valueOf(roleStr)); // String → Enum
                    ur.setUser(user);                 // set back-reference
                    return ur;
                })
                .collect(Collectors.toSet());

        user.setRoles(updatedRoles);
        AppUser updated = userRepository.save(user);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteUser(String username, String adminUsername) {
        AppUser admin = findUser(adminUsername);
        boolean isAdminOrSuper = admin.getRoles().stream()
                .anyMatch(r -> r.getRole() == Role.ROLE_ADMIN || r.getRole() == Role.ROLE_SUPERADMIN);
        if (!isAdminOrSuper) {
            throw new RuntimeException("Only admins or superadmins can perform this action");
        }

        AppUser user = findUser(username);
        userRepository.delete(user);
    }

    // --- Helper methods ---
    private AppUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private UserResponseDTO toDTO(AppUser user) {
        // Convert addresses to AddressDTO
        List<com.sa.M_Mart.dto.AddressDTO> addresses =
                user.getAddresses() != null
                        ? user.getAddresses().stream()
                        .map(addr -> new com.sa.M_Mart.dto.AddressDTO(
                                addr.getFullName(),
                                addr.getPhone(),
                                addr.getStreet(),
                                addr.getCity(),
                                addr.getState(),
                                addr.getCountry(),
                                addr.getZip()
                        ))
                        .collect(Collectors.toList())
                        : List.of();

        Set<Role> roles = (user.getRoles() != null ? user.getRoles() : Set.of())
                .stream()
                .map(ur -> ((UserRole) ur).getRole()) // keep as Role enum
                .collect(Collectors.toSet());
        if (roles.contains(Role.ROLE_ADMIN) || roles.contains(Role.ROLE_SUPERADMIN)) {
            addresses = List.of(); // override addresses for admins/superadmins
        }
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                addresses,
                roles,
                user.isVerified()
        );
    }
}

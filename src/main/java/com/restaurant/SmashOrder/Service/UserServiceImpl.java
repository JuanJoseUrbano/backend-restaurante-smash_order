package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.DTO.LoginDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.Role;
import com.restaurant.SmashOrder.Entity.User;
import com.restaurant.SmashOrder.Repository.RoleRepository;
import com.restaurant.SmashOrder.Repository.UserRepository;
import com.restaurant.SmashOrder.IService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String USERNAME_EXISTS = "Username already exists, try another";
    private static final String EMAIL_EXISTS = "Email already in use, try another";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String ROLE_NOT_FOUND = "Error: Role %s not found.";

    @Override
    public ResponseEntity<String> registerUser(User user) {
        try {
            String validationError = validateUserUniqueness(user.getUserName(), user.getEmail(), null);
            if (validationError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
            }

            Set<Role> rolesAsignados = processRoles(user.getRoles());

            User newUser = User.builder()
                    .userName(user.getUserName())
                    .name(user.getName())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .roles(rolesAsignados)
                    .build();

            userRepository.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<UserDTO> loginUser(LoginDTO loginDTO) {
        return userRepository.findByUserNameAndPassword(
                        loginDTO.getUsuario(),
                        loginDTO.getPassword()
                )
                .map(this::mapToUserDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToUserDTO);
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::mapToUserDTO);
    }

    @Override
    public List<UserDTO> searchUserByName(String name) {
        return userRepository.searchByName(name)
                .stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<String> updateUser(Long id, User user) {
        try {
            Optional<User> existingUserOpt = userRepository.findById(id);
            if (existingUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND + id);
            }

            User existingUser = existingUserOpt.get();

            String validationError = validateUserUniqueness(user.getUserName(), user.getEmail(), id);
            if (validationError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
            }

            // Procesar roles
            Set<Role> rolesAsignados = processRoles(user.getRoles());

            // Actualizar datos
            existingUser.setUserName(user.getUserName());
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setRoles(rolesAsignados);

            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                existingUser.setPassword(user.getPassword());
            }

            userRepository.save(existingUser);
            return ResponseEntity.ok("User updated successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @Override
    public Long countAllUsers() {
        return userRepository.countBy();
    }

    private String validateUserUniqueness(String username, String email, Long excludeUserId) {
        Optional<User> userByUsername = userRepository.findByUserName(username);
        if (userByUsername.isPresent() &&
                (excludeUserId == null || !userByUsername.get().getId().equals(excludeUserId))) {
            return USERNAME_EXISTS;
        }
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent() &&
                (excludeUserId == null || !userByEmail.get().getId().equals(excludeUserId))) {
            return EMAIL_EXISTS;
        }

        return null;
    }

    private Set<Role> processRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseThrow(() -> new RuntimeException(String.format(ROLE_NOT_FOUND, ROLE_USER)));
            return Set.of(defaultRole);
        }

        return roles.stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RuntimeException(String.format(ROLE_NOT_FOUND, role.getName()))))
                .collect(Collectors.toSet());
    }

    private UserDTO mapToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getName(),
                user.getEmail(),
                user.getRoles()
        );
    }
}

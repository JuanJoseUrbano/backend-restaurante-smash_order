package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.DTO.JWTAuthResponseDTO;
import com.restaurant.SmashOrder.DTO.LoginDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.Role;
import com.restaurant.SmashOrder.Entity.User;
import com.restaurant.SmashOrder.Repository.RoleRepository;
import com.restaurant.SmashOrder.Repository.UserRepository;
import com.restaurant.SmashOrder.IService.UserService;
import com.restaurant.SmashOrder.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

            String encryptedPassword = passwordEncoder.encode(user.getPassword());

            User newUser = User.builder()
                    .userName(user.getUserName())
                    .name(user.getName())
                    .email(user.getEmail())
                    .password(encryptedPassword)
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

            Set<Role> rolesAsignados = processRoles(user.getRoles());

            existingUser.setUserName(user.getUserName());
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setRoles(rolesAsignados);

            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                String encryptedPassword = passwordEncoder.encode(user.getPassword());
                existingUser.setPassword(encryptedPassword);
            }

            userRepository.save(existingUser);

            return ResponseEntity.ok("User updated successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating user: " + e.getMessage());
        }
    }
    @Override
    public ResponseEntity<JWTAuthResponseDTO> authenticateUser(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUserName(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUserName(loginDTO.getUserName()).orElseThrow();

        UserDTO userDTO = mapToUserDTO(user);

        JWTAuthResponseDTO jwtResponse = new JWTAuthResponseDTO();
        jwtResponse.setAccessToken(token);
        jwtResponse.setUser(userDTO);

        return ResponseEntity.ok(jwtResponse);
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
    public Optional<UserDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        return userOpt.map(this::mapToUserDTO);
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
    public Optional<UserDTO> findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .map(this::mapToUserDTO);
    }


    @Override
    public ResponseEntity<String> deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + id);
        }

        User user = userOpt.get();

        user.getRoles().clear();
        userRepository.save(user);

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

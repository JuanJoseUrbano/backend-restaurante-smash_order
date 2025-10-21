package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.DTO.JWTAuthResponseDTO;
import com.restaurant.SmashOrder.DTO.LoginDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {
    ResponseEntity<String> registerUser(User user);
    ResponseEntity<JWTAuthResponseDTO> authenticateUser( LoginDTO loginDTO);
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getCurrentUser(Authentication authentication);
    Optional<UserDTO> getUserByEmail(String email);
    List<UserDTO> searchUserByName(String name);
    Optional<UserDTO> findByUserName(String userName);
    ResponseEntity<String> updateUser(Long id, User user);
    ResponseEntity<String> deleteUser(Long id);
    Long countAllUsers();
}
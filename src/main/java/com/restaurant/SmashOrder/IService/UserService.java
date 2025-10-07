package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.DTO.LoginDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    ResponseEntity<String> registerUser(User user);
    ResponseEntity<UserDTO> loginUser(LoginDTO loginDTO);
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByEmail(String email);
    List<UserDTO> searchUserByName(String name);
    ResponseEntity<String> updateUser(Long id, User user);
    ResponseEntity<String> deleteUser(Long id);
    Long countAllUsers();
}
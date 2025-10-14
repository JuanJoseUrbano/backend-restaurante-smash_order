package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.DTO.LoginDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.User;
import com.restaurant.SmashOrder.IService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserDTO> userDTO = userService.getUserById(id);
        if (userDTO.isPresent()) {
            return ResponseEntity.ok(userDTO.get());
        } else {
            return ResponseEntity.status(404).body("User not found with id: " + id);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<UserDTO> userDTO = userService.getUserByEmail(email);
        if (userDTO.isPresent()) {
            return ResponseEntity.ok(userDTO.get());
        } else {
            return ResponseEntity.status(404).body("User not found with email:  " + email);
        }
    }
    @GetMapping("/search")
    public List<UserDTO> searchUsers(@RequestParam String name) {
        return userService.searchUserByName(StringUtils.capitalize(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countAllProducts() {
        return ResponseEntity.ok( userService.countAllUsers());
    }
}
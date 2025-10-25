package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.DTO.JWTAuthResponseDTO;
import com.restaurant.SmashOrder.DTO.LoginDTO;
import com.restaurant.SmashOrder.Entity.User;
import com.restaurant.SmashOrder.IService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponseDTO> loginUser(@RequestBody LoginDTO loginDTO) {
        return userService.authenticateUser(loginDTO);
    }
}

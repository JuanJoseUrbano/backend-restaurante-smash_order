package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.IService.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            passwordResetService.sendResetLink(email);
            return ResponseEntity.ok("Si el correo está registrado, se enviará un enlace de recuperación.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            boolean success = passwordResetService.resetPassword(token, newPassword);
            if (success) {
                return ResponseEntity.ok("Contraseña restablecida correctamente.");
            } else {
                return ResponseEntity.badRequest().body("No se pudo restablecer la contraseña.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.PasswordResetToken;
import com.restaurant.SmashOrder.Entity.User;
import com.restaurant.SmashOrder.IService.PasswordResetService;
import com.restaurant.SmashOrder.Repository.PasswordResetTokenRepository;
import com.restaurant.SmashOrder.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void sendResetLink(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("No se encontró un usuario con ese correo electrónico.");
        }

        User user = userOpt.get();
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));

        tokenRepository.save(resetToken);

        // URL del frontend o tu dominio
        String resetUrl = "http://localhost:8081/reset-password/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Recuperación de contraseña");
        message.setText("Hola " + user.getName() + ",\n\nPara restablecer tu contraseña haz clic en el siguiente enlace:\n" + resetUrl +
                "\n\nEste enlace expirará en 30 minutos.");

        javaMailSender.send(message);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Token inválido.");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El token ha expirado.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
        return true;
    }
}

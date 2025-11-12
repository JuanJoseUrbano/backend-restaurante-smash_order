package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.PasswordResetToken;
import com.restaurant.SmashOrder.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);

    void deleteByToken(String token);
}

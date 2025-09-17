package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    public Optional<User> findByEmail(String email);
    public Optional<User> findByUserName(String username);
    Optional<User> findByUserNameAndPassword(String userName, String password);
}

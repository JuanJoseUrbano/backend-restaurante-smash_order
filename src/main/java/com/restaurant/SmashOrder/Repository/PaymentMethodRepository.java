package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long> {
    List<PaymentMethod> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}

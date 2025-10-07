package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long> {
    List<PaymentMethod> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}

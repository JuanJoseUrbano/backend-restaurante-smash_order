package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.Entity.PaymentMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
    List<PaymentMethod> getAllPaymentMethods();

    Optional<PaymentMethod> getPaymentMethodById(Long id);

    List<PaymentMethod> getPaymentMethodsByName(String name);

    ResponseEntity<String> createPaymentMethod(PaymentMethod paymentMethod);

    ResponseEntity<String> updatePaymentMethod(Long id, PaymentMethod paymentMethod);

    ResponseEntity<String> deletePaymentMethod(Long id);
}

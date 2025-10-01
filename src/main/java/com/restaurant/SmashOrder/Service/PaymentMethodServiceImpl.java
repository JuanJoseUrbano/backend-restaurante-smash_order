package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.IService.PaymentMethodService;
import com.restaurant.SmashOrder.Repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public Optional<PaymentMethod> getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsByName(String name) {
        return paymentMethodRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public ResponseEntity<String> createPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethodRepository.existsByName(paymentMethod.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Payment method with this name already exists.");
        }

        paymentMethodRepository.save(paymentMethod);
        return ResponseEntity.ok("Payment method created successfully");
    }

    @Override
    public ResponseEntity<String> updatePaymentMethod(Long id, PaymentMethod paymentMethod) {
        Optional<PaymentMethod> existingOpt = paymentMethodRepository.findById(id);
        if (existingOpt.isPresent()) {
            PaymentMethod existing = existingOpt.get();
            existing.setName(paymentMethod.getName() != null ? paymentMethod.getName() : existing.getName());
            existing.setDescription(paymentMethod.getDescription() != null ? paymentMethod.getDescription() : existing.getDescription());

            paymentMethodRepository.save(existing);
            return ResponseEntity.ok("Payment method updated successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Payment method with ID " + id + " not found");
    }

    @Override
    public ResponseEntity<String> deletePaymentMethod(Long id) {
        Optional<PaymentMethod> existingOpt = paymentMethodRepository.findById(id);
        if (existingOpt.isPresent()) {
            paymentMethodRepository.delete(existingOpt.get());
            return ResponseEntity.ok("Payment method deleted successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Payment method with ID " + id + " not found");
    }
}

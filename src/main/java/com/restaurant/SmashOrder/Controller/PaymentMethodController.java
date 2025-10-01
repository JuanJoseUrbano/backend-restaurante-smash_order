package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.IService.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentMethodById(@PathVariable Long id) {
        Optional<PaymentMethod> pmOpt = paymentMethodService.getPaymentMethodById(id);
        if (pmOpt.isPresent()) {
            return ResponseEntity.ok(pmOpt.get());
        } else {
            return ResponseEntity.status(404)
                    .body("Payment method not found with id: " + id);
        }
    }

    @GetMapping("/name/{name}")
    public List<PaymentMethod> getPaymentMethodsByName(@PathVariable String name) {
        return paymentMethodService.getPaymentMethodsByName(name);
    }

    @PostMapping
    public ResponseEntity<String> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        return paymentMethodService.createPaymentMethod(paymentMethod);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethod paymentMethod) {
        return paymentMethodService.updatePaymentMethod(id, paymentMethod);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaymentMethod(@PathVariable Long id) {
        return paymentMethodService.deletePaymentMethod(id);
    }
}

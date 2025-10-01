package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Invoice;
import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    List<Invoice> findByOrderCustomerId(Long customerId);
    List<Invoice> findByStatus(PaymentStatus status);
    List<Invoice> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Invoice> findByPaymentMethod(PaymentMethod paymentMethod);
    Optional<Invoice> findByReceiptNumber(String receiptNumber);

}

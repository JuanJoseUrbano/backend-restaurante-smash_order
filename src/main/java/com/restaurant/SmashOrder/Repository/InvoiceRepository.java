package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Invoice;
import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    Page<Invoice> findByOrderCustomerId(Long customerId, Pageable pageable);
    Page<Invoice> findByStatus(PaymentStatus status, Pageable pageable);
    Page<Invoice> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Invoice> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);
    Optional<Invoice> findByReceiptNumber(String receiptNumber);
    List<Invoice> findAllByOrderByPaymentDateDesc();
}

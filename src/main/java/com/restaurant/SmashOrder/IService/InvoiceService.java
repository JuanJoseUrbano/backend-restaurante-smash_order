package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.DTO.InvoiceDTO;
import com.restaurant.SmashOrder.Entity.Invoice;
import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    List<InvoiceDTO> getAllInvoices();

    Optional<InvoiceDTO> getInvoiceById(Long id);

    List<InvoiceDTO> getInvoicesByCustomerId(Long customerId);

    List<InvoiceDTO> getInvoicesByStatus(PaymentStatus status);

    List<InvoiceDTO> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<InvoiceDTO> getInvoicesByPaymentMethod(PaymentMethod paymentMethod);

    Optional<InvoiceDTO> getInvoiceByReceiptNumber(String receiptNumber);

    ResponseEntity<String> createInvoice(Invoice invoice);

    ResponseEntity<String> updateInvoice(Long id, Invoice invoice);

    ResponseEntity<String> deleteInvoice(Long id);
}

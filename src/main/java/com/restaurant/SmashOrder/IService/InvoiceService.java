package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.DTO.InvoiceDTO;
import com.restaurant.SmashOrder.Entity.Invoice;
import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    List<InvoiceDTO> getAllInvoices();
    Page<InvoiceDTO> getInvoicesPaginated(int page, int size);
    Optional<InvoiceDTO> getInvoiceById(Long id);
    Optional<InvoiceDTO> getInvoiceByReceiptNumber(String receiptNumber);
    Page<InvoiceDTO> getInvoicesByCustomerIdPaginated(Long customerId, int page, int size);
    Page<InvoiceDTO> getInvoicesByStatusPaginated(PaymentStatus status, int page, int size);
    Page<InvoiceDTO> getInvoicesByDateRangePaginated(LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    Page<InvoiceDTO> getInvoicesByPaymentMethodPaginated(PaymentMethod paymentMethod, int page, int size);
    ResponseEntity<String> createInvoice(Invoice invoice);
    ResponseEntity<String> updateInvoice(Long id, Invoice invoice);
    ResponseEntity<String> deleteInvoice(Long id);
}

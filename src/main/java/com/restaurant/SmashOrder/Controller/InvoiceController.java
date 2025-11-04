package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.DTO.InvoiceDTO;
import com.restaurant.SmashOrder.Entity.Invoice;
import com.restaurant.SmashOrder.Entity.PaymentMethod;
import com.restaurant.SmashOrder.IService.InvoiceService;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<InvoiceDTO>> getInvoicesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<InvoiceDTO> invoices = invoiceService.getInvoicesPaginated(page, size);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
        Optional<InvoiceDTO> invoiceOpt = invoiceService.getInvoiceById(id);
        if (invoiceOpt.isPresent()) {
            return ResponseEntity.ok(invoiceOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Invoice with ID " + id + " not found");
        }
    }

    @GetMapping("/customer/{customerId}/paginated")
    public Page<InvoiceDTO> getInvoicesByCustomerPaginated(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return invoiceService.getInvoicesByCustomerIdPaginated(customerId, page, size);
    }

    @GetMapping("/status/{status}/paginated")
    public Page<InvoiceDTO> getInvoicesByStatusPaginated(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return invoiceService.getInvoicesByStatusPaginated(status, page, size);
    }

    @GetMapping("/date-range/paginated")
    public Page<InvoiceDTO> getInvoicesByDateRangePaginated(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return invoiceService.getInvoicesByDateRangePaginated(start, end, page, size);
    }

    @GetMapping("/payment-method/{paymentMethodId}/paginated")
    public Page<InvoiceDTO> getInvoicesByPaymentMethodPaginated(
            @PathVariable Long paymentMethodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaymentMethod pm = new PaymentMethod();
        pm.setId(paymentMethodId);
        return invoiceService.getInvoicesByPaymentMethodPaginated(pm, page, size);
    }

    @GetMapping("/receipt/{receiptNumber}")
    public ResponseEntity<InvoiceDTO> getInvoiceByReceipt(@PathVariable String receiptNumber) {
        Optional<InvoiceDTO> invoiceOpt = invoiceService.getInvoiceByReceiptNumber(receiptNumber);
        return invoiceOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createInvoice(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        return invoiceService.updateInvoice(id, invoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        return invoiceService.deleteInvoice(id);
    }
}
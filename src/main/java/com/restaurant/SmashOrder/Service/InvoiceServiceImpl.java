    package com.restaurant.SmashOrder.Service;

    import com.restaurant.SmashOrder.DTO.InvoiceDTO;
    import com.restaurant.SmashOrder.DTO.OrderSummaryDTO;
    import com.restaurant.SmashOrder.DTO.UserDTO;
    import com.restaurant.SmashOrder.Entity.Invoice;
    import com.restaurant.SmashOrder.Entity.Order;
    import com.restaurant.SmashOrder.Entity.PaymentMethod;
    import com.restaurant.SmashOrder.IService.InvoiceService;
    import com.restaurant.SmashOrder.Repository.InvoiceRepository;
    import com.restaurant.SmashOrder.Repository.OrderRepository;
    import com.restaurant.SmashOrder.Repository.PaymentMethodRepository;
    import com.restaurant.SmashOrder.Utils.PaymentStatus;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;

    @Service
    @RequiredArgsConstructor
    public class InvoiceServiceImpl implements InvoiceService {

        private final InvoiceRepository invoiceRepository;
        private final PaymentMethodRepository paymentMethodRepository;
        private final OrderRepository orderRepository;

        private OrderSummaryDTO mapToOrderSummaryDTO(Order order) {
            if (order == null) return null;

            return new OrderSummaryDTO(order.getId(),new UserDTO(
                            order.getCustomer().getId(),
                            order.getCustomer().getUserName(),
                            order.getCustomer().getName(),
                            order.getCustomer().getEmail(),
                            order.getCustomer().getRoles()),
                    order.getTable(),
                    order.getTotal(),
                    order.getDate(),
                    order.getStatus()
            );
        }

        private InvoiceDTO mapToInvoiceDTO(Invoice invoice) {
            if (invoice == null) return null;

            return new InvoiceDTO(
                    invoice.getId(),
                    mapToOrderSummaryDTO(invoice.getOrder()),
                    invoice.getPaymentDate(),
                    invoice.getReceiptNumber(),
                    invoice.getStatus(),
                    invoice.getTotal(),
                    invoice.getPaymentMethod(),
                    invoice.getCreatedAt()
            );
        }
        @Override
        public List<InvoiceDTO> getAllInvoices() {
            return invoiceRepository.findAllByOrderByPaymentDateDesc()
                    .stream()
                    .map(this::mapToInvoiceDTO)
                    .toList();
        }

        @Override
        public Optional<InvoiceDTO> getInvoiceById(Long id) {
            return invoiceRepository.findById(id)
                    .map(this::mapToInvoiceDTO);
        }

        @Override
        public List<InvoiceDTO> getInvoicesByCustomerId(Long customerId) {
            return invoiceRepository.findByOrderCustomerId(customerId)
                    .stream()
                    .map(this::mapToInvoiceDTO)
                    .toList();
        }

        @Override
        public List<InvoiceDTO> getInvoicesByStatus(PaymentStatus status) {
            return invoiceRepository.findByStatus(status)
                    .stream()
                    .map(this::mapToInvoiceDTO)
                    .toList();
        }

        @Override
        public List<InvoiceDTO> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
            return invoiceRepository.findByPaymentDateBetween(startDate, endDate)
                    .stream()
                    .map(this::mapToInvoiceDTO)
                    .toList();
        }

        @Override
        public List<InvoiceDTO> getInvoicesByPaymentMethod(PaymentMethod paymentMethod) {
            return invoiceRepository.findByPaymentMethod(paymentMethod)
                    .stream()
                    .map(this::mapToInvoiceDTO)
                    .toList();
        }

        @Override
        public Optional<InvoiceDTO> getInvoiceByReceiptNumber(String receiptNumber) {
            return invoiceRepository.findByReceiptNumber(receiptNumber)
                    .map(this::mapToInvoiceDTO);
        }
        @Override
        public ResponseEntity<String> createInvoice(Invoice invoice) {
            try {
                if (invoice.getOrder() == null || !orderRepository.existsById(invoice.getOrder().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid order: Order not found.");
                }

                if (invoice.getPaymentMethod() == null || !paymentMethodRepository.existsById(invoice.getPaymentMethod().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid payment method.");
                }

                Order order = orderRepository.findById(invoice.getOrder().getId()).orElseThrow();

                invoice.setTotal(order.getTotal());

                if (invoice.getStatus() == null) {
                    invoice.setStatus(PaymentStatus.PENDING);
                }

                if (invoice.getStatus() == PaymentStatus.PAID) {
                    invoice.setPaymentDate(LocalDateTime.now());
                } else {
                    invoice.setPaymentDate(null);
                }
                invoice.setReceiptNumber("RCPT-" + System.currentTimeMillis());
                invoice.setCreatedAt(LocalDateTime.now());

                invoiceRepository.save(invoice);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Invoice created successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error creating invoice: " + e.getMessage());
            }
        }

        @Override
        public ResponseEntity<String> updateInvoice(Long id, Invoice updatedInvoice) {
            try {
                Invoice existingInvoice = invoiceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Invoice not found"));

                if (updatedInvoice.getOrder() == null ||
                        !orderRepository.existsById(updatedInvoice.getOrder().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid order: Order not found.");
                }

                if (updatedInvoice.getPaymentMethod() == null ||
                        !paymentMethodRepository.existsById(updatedInvoice.getPaymentMethod().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid payment method.");
                }

                Order order = orderRepository.findById(updatedInvoice.getOrder().getId())
                        .orElseThrow(() -> new RuntimeException("Order not found"));
                PaymentMethod paymentMethod = paymentMethodRepository
                        .findById(updatedInvoice.getPaymentMethod().getId())
                        .orElseThrow(() -> new RuntimeException("Payment method not found"));

                existingInvoice.setOrder(order);
                existingInvoice.setPaymentMethod(paymentMethod);
                existingInvoice.setTotal(order.getTotal());

                PaymentStatus status = updatedInvoice.getStatus() != null
                        ? updatedInvoice.getStatus()
                        : existingInvoice.getStatus();
                existingInvoice.setStatus(status);

                if (status == PaymentStatus.PAID) {
                    existingInvoice.setPaymentDate(LocalDateTime.now());
                } else {
                    if (existingInvoice.getPaymentDate() == null) {
                        existingInvoice.setPaymentDate(existingInvoice.getCreatedAt() != null
                                ? existingInvoice.getCreatedAt()
                                : LocalDateTime.now());
                    }
                }

                if (updatedInvoice.getReceiptNumber() != null &&
                        !updatedInvoice.getReceiptNumber().isEmpty()) {
                    existingInvoice.setReceiptNumber(updatedInvoice.getReceiptNumber());
                } else if (existingInvoice.getReceiptNumber() == null) {
                    existingInvoice.setReceiptNumber("RCPT-" + System.currentTimeMillis());
                }

                if (existingInvoice.getCreatedAt() == null) {
                    existingInvoice.setCreatedAt(LocalDateTime.now());
                }

                invoiceRepository.save(existingInvoice);

                return ResponseEntity.ok("Invoice updated successfully");

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error updating invoice: " + e.getMessage());
            }
        }

        @Override
        public ResponseEntity<String> deleteInvoice(Long id) {
            return invoiceRepository.findById(id).map(invoice -> {
                Order order = invoice.getOrder();
                if (order != null) {
                    order.setInvoice(null);
                    orderRepository.save(order);
                }

                invoiceRepository.delete(invoice);

                return ResponseEntity.ok("Invoice deleted successfully, order preserved");
            }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Invoice with ID " + id + " not found"));
        }
    }
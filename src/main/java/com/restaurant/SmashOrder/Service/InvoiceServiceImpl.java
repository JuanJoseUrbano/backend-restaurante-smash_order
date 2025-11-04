    package com.restaurant.SmashOrder.Service;

    import com.restaurant.SmashOrder.DTO.InvoiceDTO;
    import com.restaurant.SmashOrder.DTO.OrderSummaryDTO;
    import com.restaurant.SmashOrder.DTO.UserDTO;
    import com.restaurant.SmashOrder.Entity.Invoice;
    import com.restaurant.SmashOrder.Entity.Notification;
    import com.restaurant.SmashOrder.Entity.Order;
    import com.restaurant.SmashOrder.Entity.PaymentMethod;
    import com.restaurant.SmashOrder.IService.InvoiceService;
    import com.restaurant.SmashOrder.Repository.InvoiceRepository;
    import com.restaurant.SmashOrder.Repository.NotificationRepository;
    import com.restaurant.SmashOrder.Repository.OrderRepository;
    import com.restaurant.SmashOrder.Repository.PaymentMethodRepository;
    import com.restaurant.SmashOrder.Utils.PaymentStatus;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
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
        private final NotificationRepository notificationRepository;

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
        public Page<InvoiceDTO> getInvoicesPaginated(int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            return invoiceRepository.findAll(pageable)
                    .map(this::mapToInvoiceDTO);
        }


        @Override
        public Optional<InvoiceDTO> getInvoiceById(Long id) {
            return invoiceRepository.findById(id)
                    .map(this::mapToInvoiceDTO);
        }


        @Override
        public Optional<InvoiceDTO> getInvoiceByReceiptNumber(String receiptNumber) {
            return invoiceRepository.findByReceiptNumber(receiptNumber)
                    .map(this::mapToInvoiceDTO);
        }

        @Override
        public Page<InvoiceDTO> getInvoicesByCustomerIdPaginated(Long customerId, int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
            return invoiceRepository.findByOrderCustomerId(customerId, pageable)
                    .map(this::mapToInvoiceDTO);
        }

        @Override
        public Page<InvoiceDTO> getInvoicesByStatusPaginated(PaymentStatus status, int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
            return invoiceRepository.findByStatus(status, pageable)
                    .map(this::mapToInvoiceDTO);
        }

        @Override
        public Page<InvoiceDTO> getInvoicesByDateRangePaginated(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
            return invoiceRepository.findByPaymentDateBetween(startDate, endDate, pageable)
                    .map(this::mapToInvoiceDTO);
        }

        @Override
        public Page<InvoiceDTO> getInvoicesByPaymentMethodPaginated(PaymentMethod paymentMethod, int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
            return invoiceRepository.findByPaymentMethod(paymentMethod, pageable)
                    .map(this::mapToInvoiceDTO);
        }

        @Override
        public ResponseEntity<String> createInvoice(Invoice invoice) {
            try {
                if (invoice.getOrder() == null || !orderRepository.existsById(invoice.getOrder().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Orden inválida: no se encontró la orden asociada.");
                }

                if (invoice.getPaymentMethod() == null || !paymentMethodRepository.existsById(invoice.getPaymentMethod().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Método de pago inválido.");
                }

                Order order = orderRepository.findById(invoice.getOrder().getId())
                        .orElseThrow(() -> new RuntimeException("No se encontró la orden."));

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

                Invoice savedInvoice = invoiceRepository.save(invoice);

                String message;
                String type;

                switch (invoice.getStatus()) {
                    case PAID:
                        message = "Se ha registrado el pago de la orden #" + order.getId() +
                                " por un total de $" + order.getTotal() + ".";
                        type = "Pago completado";
                        break;

                    case PENDING:
                        message = "Se ha creado una factura pendiente para la orden #" + order.getId() +
                                " con un total de $" + order.getTotal() + ".";
                        type = "Factura pendiente";
                        break;

                    case CANCELLED:
                        message = "El pago de la orden #" + order.getId() + " ha sido cancelado.";
                        type = "Pago cancelado";
                        break;

                    default:
                        message = "El pago de la orden #" + order.getId() + " ha sido Reembolsado.";
                        type = "Pago Reembolsado";
                        break;
                }
                Notification notification = new Notification();
                notification.setType(type);
                notification.setMessage(message);
                notification.setOrder(order);
                notificationRepository.save(notification);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Factura creada exitosamente con ID: " + savedInvoice.getId());

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear la factura: " + e.getMessage());
            }
        }


        @Override
        public ResponseEntity<String> updateInvoice(Long id, Invoice updatedInvoice) {
            try {
                Invoice existingInvoice = invoiceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("No se encontró la factura con ID: " + id));

                if (updatedInvoice.getOrder() == null ||
                        !orderRepository.existsById(updatedInvoice.getOrder().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Orden inválida: no se encontró la orden asociada.");
                }

                if (updatedInvoice.getPaymentMethod() == null ||
                        !paymentMethodRepository.existsById(updatedInvoice.getPaymentMethod().getId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Método de pago inválido.");
                }

                Order order = orderRepository.findById(updatedInvoice.getOrder().getId())
                        .orElseThrow(() -> new RuntimeException("Orden no encontrada."));
                PaymentMethod paymentMethod = paymentMethodRepository
                        .findById(updatedInvoice.getPaymentMethod().getId())
                        .orElseThrow(() -> new RuntimeException("Método de pago no encontrado."));

                PaymentStatus previousStatus = existingInvoice.getStatus();

                existingInvoice.setOrder(order);
                existingInvoice.setPaymentMethod(paymentMethod);
                existingInvoice.setTotal(order.getTotal());

                PaymentStatus newStatus = updatedInvoice.getStatus() != null
                        ? updatedInvoice.getStatus()
                        : existingInvoice.getStatus();

                existingInvoice.setStatus(newStatus);

                if (newStatus == PaymentStatus.PAID) {
                    existingInvoice.setPaymentDate(LocalDateTime.now());
                } else if (newStatus == PaymentStatus.PENDING) {
                    existingInvoice.setPaymentDate(null);
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

                Invoice savedInvoice = invoiceRepository.save(existingInvoice);

                if (previousStatus != newStatus) {
                    String message;
                    String type;

                    switch (newStatus) {
                        case PAID:
                            message = "Se ha registrado el pago de la orden #" + order.getId() +
                                    " por un total de $" + order.getTotal() + ".";
                            type = "Pago completado";
                            break;

                        case PENDING:
                            message = "El pago de la orden #" + order.getId() + " ha sido marcado como pendiente.";
                            type = "Pago pendiente";
                            break;

                        case CANCELLED:
                            message = "El pago de la orden #" + order.getId() + " ha sido cancelado.";
                            type = "Pago cancelado";
                            break;

                        case REFUNDED:
                            message = "Se ha registrado un reembolso para la orden #" + order.getId() + ".";
                            type = "Pago reembolsado";
                            break;

                        default:
                            message = "El estado del pago de la orden #" + order.getId() + " ha sido actualizado.";
                            type = "Actualización de pago";
                            break;
                    }

                    Notification notification = new Notification();
                    notification.setType(type);
                    notification.setMessage(message);
                    notification.setOrder(order);
                    notificationRepository.save(notification);
                }

                return ResponseEntity.ok("Factura actualizada correctamente con ID: " + savedInvoice.getId());

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al actualizar la factura: " + e.getMessage());
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
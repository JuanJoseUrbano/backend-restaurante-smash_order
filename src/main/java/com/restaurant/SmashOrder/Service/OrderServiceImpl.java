package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.DTO.OrderDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.*;
import com.restaurant.SmashOrder.Repository.*;
import com.restaurant.SmashOrder.IService.OrderService;
import com.restaurant.SmashOrder.Utils.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByDateDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<OrderDTO> getOrdersWithoutInvoice() {
        return orderRepository.findOrdersWithoutInvoice()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByDate(LocalDateTime date) {
        return orderRepository.findByDate(date)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<String> createOrder(Order order) {
        try {
            if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
                return ResponseEntity.badRequest().body("La orden debe contener al menos un producto");
            }

            Map<Long, OrderDetail> combinedDetails = new HashMap<>();

            for (OrderDetail detail : order.getOrderDetails()) {
                Optional<Product> productOpt = productRepository.findById(detail.getProduct().getId());
                if (productOpt.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body("Producto no encontrado con ID: " + detail.getProduct().getId());
                }

                Product product = productOpt.get();
                Long productId = product.getId();

                if (combinedDetails.containsKey(productId)) {
                    OrderDetail existingDetail = combinedDetails.get(productId);
                    existingDetail.setQuantity(existingDetail.getQuantity() + detail.getQuantity());
                    existingDetail.setSubtotal(product.getPrice()
                            .multiply(BigDecimal.valueOf(existingDetail.getQuantity())));
                } else {
                    detail.setProduct(product);
                    detail.setSubtotal(product.getPrice()
                            .multiply(BigDecimal.valueOf(detail.getQuantity())));
                    combinedDetails.put(productId, detail);
                }
            }

            BigDecimal total = BigDecimal.ZERO;
            for (OrderDetail detail : combinedDetails.values()) {
                detail.setOrder(order);
                total = total.add(detail.getSubtotal());
            }

            order.setOrderDetails(new ArrayList<>(combinedDetails.values()));
            order.setTotal(total);
            order.setDate(LocalDateTime.now());

            if (order.getInvoice() == null || order.getInvoice().getPaymentMethod() == null) {
                return ResponseEntity.badRequest().body("Debe especificar un método de pago en la factura.");
            }

            Long paymentMethodId = order.getInvoice().getPaymentMethod().getId();
            Optional<PaymentMethod> paymentMethodOpt = paymentMethodRepository.findById(paymentMethodId);
            if (paymentMethodOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Método de pago no encontrado con ID: " + paymentMethodId);
            }

            PaymentMethod paymentMethod = paymentMethodOpt.get();

            Invoice invoice = new Invoice();
            invoice.setPaymentMethod(paymentMethod);

            if (order.getInvoice().getStatus() == PaymentStatus.PAID) {
                invoice.setPaymentDate(LocalDateTime.now());
            } else {
                invoice.setPaymentDate(null);
            }
            invoice.setStatus(order.getInvoice().getStatus() != null
                            ? order.getInvoice().getStatus()
                            : PaymentStatus.PENDING
            );
            invoice.setTotal(total);
            invoice.setReceiptNumber("RCPT-" + System.currentTimeMillis());
            invoice.setCreatedAt(LocalDateTime.now());

            invoice.setOrder(order);
            order.setInvoice(invoice);

            Order savedOrder = orderRepository.save(order);

            Notification notification = new Notification();
            notification.setType("Nueva orden creada");
            notification.setMessage("Se ha creado una orden con ID: " + savedOrder.getId() + " por un total de $" + total);
            notification.setOrder(savedOrder);
            notificationRepository.save(notification);

            return ResponseEntity.ok(
                    "Orden creada con ID: " + savedOrder.getId() +
                            ", total: $" + total +
                            " y factura generada con ID: " + savedOrder.getInvoice().getId()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la orden: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateOrder(Long id, Order updatedOrder) {
        try {
            Optional<Order> existingOrderOpt = orderRepository.findById(id);
            if (existingOrderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró la orden con ID: " + id);
            }

            Order existingOrder = existingOrderOpt.get();

            String previousStatus = existingOrder.getStatus();

            existingOrder.setCustomer(updatedOrder.getCustomer());
            existingOrder.setTable(updatedOrder.getTable());
            existingOrder.setStatus(updatedOrder.getStatus());
            existingOrder.setDate(LocalDateTime.now());

            existingOrder.getOrderDetails().clear();
            Map<Long, OrderDetail> combinedDetails = new HashMap<>();

            for (OrderDetail detail : updatedOrder.getOrderDetails()) {
                Long productId = detail.getProduct().getId();

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

                if (combinedDetails.containsKey(productId)) {
                    OrderDetail existingDetail = combinedDetails.get(productId);
                    existingDetail.setQuantity(existingDetail.getQuantity() + detail.getQuantity());
                    existingDetail.setSubtotal(product.getPrice()
                            .multiply(BigDecimal.valueOf(existingDetail.getQuantity())));
                } else {
                    OrderDetail newDetail = new OrderDetail();
                    newDetail.setProduct(product);
                    newDetail.setQuantity(detail.getQuantity());
                    newDetail.setSubtotal(product.getPrice()
                            .multiply(BigDecimal.valueOf(detail.getQuantity())));
                    newDetail.setOrder(existingOrder);
                    combinedDetails.put(productId, newDetail);
                }
            }

            existingOrder.getOrderDetails().addAll(combinedDetails.values());

            BigDecimal total = existingOrder.getOrderDetails().stream()
                    .map(OrderDetail::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            existingOrder.setTotal(total);

            Invoice existingInvoice = existingOrder.getInvoice();
            if (existingInvoice != null) {
                existingInvoice.setTotal(total);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La orden no tiene una factura asociada para actualizar.");
            }

            Order savedOrder = orderRepository.save(existingOrder);

            Map<String, String> statusNames = Map.of(
                    "PENDING", "Pendiente",
                    "IN_PROGRESS", "En proceso",
                    "COMPLETED", "Completado",
                    "CANCELLED", "Cancelado"
            );

            String estadoAnterior = statusNames.getOrDefault(previousStatus, previousStatus != null ? previousStatus : "Desconocido");
            String estadoNuevo = statusNames.getOrDefault(updatedOrder.getStatus(), updatedOrder.getStatus());

            String mensaje;

            if (!Objects.equals(previousStatus, updatedOrder.getStatus())) {
                mensaje = "El estado de la orden #" + savedOrder.getId() +
                        " cambió de " + estadoAnterior + " a " + estadoNuevo +
                        ". El nuevo total es $" + total + ".";
            } else {
                mensaje = "La orden #" + savedOrder.getId() +
                        " ha sido actualizada. El total recalculado es $" + total + ".";
            }

            Notification notification = new Notification();
            notification.setType("Actualización de orden");
            notification.setMessage(mensaje);
            notification.setOrder(savedOrder);
            notificationRepository.save(notification);

            return ResponseEntity.ok("Orden actualizada correctamente con ID: " + savedOrder.getId()
                    + ", nuevo total: $" + total);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la orden: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.ok("Orden eliminada con éxito");
    }

    @Override
    public Long countAllOrders() {
        return orderRepository.countAllOrders();
    }

    @Override
    public Long countOrdersByCustomer(Long customerId) {
        return orderRepository.countOrdersByCustomer(customerId);
    }

    private OrderDTO mapToDTO(Order order) {
        UserDTO customerDTO = new UserDTO(
                order.getCustomer().getId(),
                order.getCustomer().getUserName(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getCustomer().getRoles()
        );

        return new OrderDTO(
                order.getId(),
                customerDTO,
                order.getTable(),
                order.getOrderDetails(),
                order.getTotal(),
                order.getDate(),
                order.getStatus()
        );
    }
}

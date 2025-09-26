package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.Order;
import com.restaurant.SmashOrder.Entity.OrderDetail;
import com.restaurant.SmashOrder.Entity.Product;
import com.restaurant.SmashOrder.Repository.OrderDetailRepository;
import com.restaurant.SmashOrder.Repository.OrderRepository;
import com.restaurant.SmashOrder.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public ResponseEntity<String> createOrder(Order order) {
        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            return ResponseEntity.badRequest().body("La orden debe contener al menos un producto");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (OrderDetail detail : order.getOrderDetails()) {
            Optional<Product> productOpt = productRepository.findById(detail.getProduct().getId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + detail.getProduct().getId());
            }

            Product product = productOpt.get();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));

            detail.setProduct(product);
            detail.setSubtotal(subtotal);
            detail.setOrder(order);

            total = total.add(subtotal);
        }

        order.setTotal(total);
        if (order.getDate() == null) {
            order.setDate(LocalDateTime.now());
        }

        Order savedOrder = orderRepository.save(order);
        orderDetailRepository.saveAll(order.getOrderDetails());

        return ResponseEntity.ok("Orden creada con ID: " + savedOrder.getId() + " y total: $" + total);
    }

    @Override
    public ResponseEntity<String> updateOrder(Long id, Order updatedOrder) {
        Optional<Order> existingOrderOpt = orderRepository.findById(id);
        if (existingOrderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order existingOrder = existingOrderOpt.get();

        existingOrder.setCustomer(updatedOrder.getCustomer());
        existingOrder.setEmployee(updatedOrder.getEmployee());
        existingOrder.setTable(updatedOrder.getTable());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setDate(updatedOrder.getDate() != null ? updatedOrder.getDate() : LocalDateTime.now());

        // ✅ FIX: No reemplazar la lista, sino limpiarla y volverla a poblar
        existingOrder.getOrderDetails().clear();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail detail : updatedOrder.getOrderDetails()) {
            Optional<Product> productOpt = productRepository.findById(detail.getProduct().getId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + detail.getProduct().getId());
            }

            Product product = productOpt.get();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));

            detail.setProduct(product);
            detail.setSubtotal(subtotal);
            detail.setOrder(existingOrder);

            existingOrder.getOrderDetails().add(detail);
            total = total.add(subtotal);
        }

        existingOrder.setTotal(total);

        orderRepository.save(existingOrder);

        return ResponseEntity.ok("Orden actualizada con ID: " + existingOrder.getId() + " y nuevo total: $" + total);
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
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getOrdersByDate(LocalDateTime date) {
        return orderRepository.findByDate(date);
    }
}

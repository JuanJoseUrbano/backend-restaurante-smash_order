package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.Order;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    ResponseEntity<String> createOrder(Order order);

    ResponseEntity<String> updateOrder(Long id, Order order);

    ResponseEntity<String> deleteOrder(Long id);
    List<Order> getOrdersByCustomer(Long customerId);

    List<Order> getOrdersByStatus(String status);

    List<Order> getOrdersByDate(LocalDateTime date);
}

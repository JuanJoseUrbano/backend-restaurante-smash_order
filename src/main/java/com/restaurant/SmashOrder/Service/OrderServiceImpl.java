package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.DTO.OrderDTO;
import com.restaurant.SmashOrder.DTO.UserDTO;
import com.restaurant.SmashOrder.Entity.Order;
import com.restaurant.SmashOrder.Entity.OrderDetail;
import com.restaurant.SmashOrder.Entity.Product;
import com.restaurant.SmashOrder.Repository.OrderDetailRepository;
import com.restaurant.SmashOrder.Repository.OrderRepository;
import com.restaurant.SmashOrder.Repository.ProductRepository;
import com.restaurant.SmashOrder.IService.OrderService;
import lombok.RequiredArgsConstructor;
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
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
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
        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            return ResponseEntity.badRequest().body("La orden debe contener al menos un producto");
        }

        Map<Long, OrderDetail> combinedDetails = new HashMap<>();

        for (OrderDetail detail : order.getOrderDetails()) {
            Optional<Product> productOpt = productRepository.findById(detail.getProduct().getId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado con ID: " + detail.getProduct().getId());
            }

            Product product = productOpt.get();
            Long productId = product.getId();

            if (combinedDetails.containsKey(productId)) {
                OrderDetail existingDetail = combinedDetails.get(productId);
                existingDetail.setQuantity(existingDetail.getQuantity() + detail.getQuantity());

                BigDecimal newSubtotal = product.getPrice().multiply(BigDecimal.valueOf(existingDetail.getQuantity()));
                existingDetail.setSubtotal(newSubtotal);
            } else {
                detail.setProduct(product);

                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
                detail.setSubtotal(subtotal);

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
        existingOrder.setTable(updatedOrder.getTable());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setDate(updatedOrder.getDate() != null ? updatedOrder.getDate() : LocalDateTime.now());

        existingOrder.getOrderDetails().clear();

        Map<Long, OrderDetail> combinedDetails = new HashMap<>();

        for (OrderDetail detail : updatedOrder.getOrderDetails()) {
            Long productId = detail.getProduct().getId();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

            if (combinedDetails.containsKey(productId)) {
                OrderDetail existingDetail = combinedDetails.get(productId);
                existingDetail.setQuantity(existingDetail.getQuantity() + detail.getQuantity());

                BigDecimal newSubtotal = product.getPrice().multiply(BigDecimal.valueOf(existingDetail.getQuantity()));
                existingDetail.setSubtotal(newSubtotal);
            } else {
                OrderDetail newDetail = new OrderDetail();
                newDetail.setProduct(product);
                newDetail.setQuantity(detail.getQuantity());
                newDetail.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
                newDetail.setOrder(existingOrder);

                combinedDetails.put(productId, newDetail);
            }
        }

        existingOrder.getOrderDetails().addAll(combinedDetails.values());

        BigDecimal total = existingOrder.getOrderDetails().stream()
                .map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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

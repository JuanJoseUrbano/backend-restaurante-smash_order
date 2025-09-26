package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.OrderDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    List<OrderDetail> getAllOrderDetails();

    Optional<OrderDetail> getOrderDetailById(Long id);

    ResponseEntity<String> addOrderDetail(Long orderId, List<OrderDetail> details);

    ResponseEntity<String> updateOrderDetail(Long id, OrderDetail detail);

    ResponseEntity<String> deleteOrderDetail(Long id);
}

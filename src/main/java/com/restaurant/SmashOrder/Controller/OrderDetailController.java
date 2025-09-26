package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.Entity.OrderDetail;
import com.restaurant.SmashOrder.Service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @GetMapping
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailService.getAllOrderDetails();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetail> getOrderDetailById(@PathVariable Long id) {
        Optional<OrderDetail> detailOpt = orderDetailService.getOrderDetailById(id);
        return detailOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<String> addOrderDetails(@PathVariable Long orderId, @RequestBody List<OrderDetail> details) {
        return orderDetailService.addOrderDetail(orderId, details);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrderDetail(@PathVariable Long id, @RequestBody OrderDetail detail) {
        return orderDetailService.updateOrderDetail(id, detail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderDetail(@PathVariable Long id) {
        return orderDetailService.deleteOrderDetail(id);
    }
}
